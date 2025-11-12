package org.leralix.tan.storage.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.leralix.tan.TownsAndNations;

public class MySqlHandler extends DatabaseHandler {

  private final String host;
  private final int port;
  private final String databaseName;
  private final String user;
  private final String password;
  private final TownsAndNations plugin;
  private HikariDataSource hikariDataSource;

  // P3.5: Metadata caching with TTL (5 minutes)
  private static final ConcurrentMap<String, String> metadataCache = new ConcurrentHashMap<>();
  private static final long METADATA_CACHE_TTL = 300_000L; // 5 minutes in milliseconds
  private static volatile long metadataCacheTime = 0;

  // OPTIMIZATION: Query limiter to prevent pool saturation
  private QueryLimiter queryLimiter;

  public MySqlHandler(
      TownsAndNations plugin,
      String host,
      int port,
      String database,
      String username,
      String password) {
    this.plugin = plugin;
    this.host = host;
    this.port = port;
    this.databaseName = database;
    this.user = username;
    this.password = password;
  }

  @Override
  public void connect() throws SQLException {

    if (host == null || databaseName == null) {
      return;
    }

    HikariConfig config = new HikariConfig();
    boolean sslEnabled = plugin.getConfig().getBoolean("database.ssl.enabled", false);
    boolean sslRequired = plugin.getConfig().getBoolean("database.ssl.require", false);
    boolean verifyServerCert =
        plugin.getConfig().getBoolean("database.ssl.verify-server-certificate", false);

    String sslParams = "useSSL=" + sslEnabled;
    if (sslEnabled) {
      sslParams += "&requireSSL=" + sslRequired;
      sslParams += "&verifyServerCertificate=" + verifyServerCert;
    }

    config.setJdbcUrl(
        String.format(
            "jdbc:mysql://%s:%d/%s?%s&allowPublicKeyRetrieval=true&serverTimezone=UTC",
            host, port, databaseName, sslParams));
    config.setUsername(user);
    config.setPassword(password);
    config.addDataSourceProperty("cachePrepStmts", "true");
    config.addDataSourceProperty("prepStmtCacheSize", "250");
    config.addDataSourceProperty("maintainTimeStats", "false");
    config.addDataSourceProperty("alwaysSendSetIsolation", "false");
    config.addDataSourceProperty("enableQueryTimeouts", "false");
    config.setPoolName("TownsAndNations-MySql-Pool");

    // PERFORMANCE FIX: Connection pool configuration optimized for high-load servers
    // Previous default of 10 was insufficient for 100+ player servers
    // Recommended: 30 for medium servers (50-100 players), 50+ for large servers (100+ players)
    config.setMaximumPoolSize(plugin.getConfig().getInt("database.pool-size", 30));
    config.setMinimumIdle(plugin.getConfig().getInt("database.min-idle", 5));
    config.setConnectionTimeout(plugin.getConfig().getLong("database.connection-timeout", 30000L));
    config.setIdleTimeout(plugin.getConfig().getLong("database.idle-timeout", 600000L));
    config.setMaxLifetime(plugin.getConfig().getLong("database.max-lifetime", 1800000L));

    // Additional HikariCP performance optimizations
    config.setLeakDetectionThreshold(
        plugin.getConfig().getLong("database.leak-detection-threshold", 60000L));

    this.dataSource = new HikariDataSource(config);

    // OPTIMIZATION: Initialize query limiter (100 concurrent queries, 5s timeout)
    this.queryLimiter = new QueryLimiter(100, 5000);
    plugin.getLogger().info("[TaN] Query limiter initialized: max 100 concurrent queries");

    // OPTIMIZATION: Initialize query batch executor (50 queries per batch, 100ms delay)
    initializeQueryBatcher(50, 100);

    createMetadataTable();
    initialize();
  }

  @Override
  public void createMetadataTable() {
    String createTableSQL =
        """
             CREATE TABLE IF NOT EXISTS tan_metadata (
                meta_key VARCHAR(255) PRIMARY KEY,
                meta_value VARCHAR(255) NOT NULL
            )
        """;

    try (Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement()) {
      stmt.execute(createTableSQL);
    } catch (SQLException e) {
      TownsAndNations.getPlugin()
          .getLogger()
          .severe("Error creating table tan_metadata: " + e.getMessage());
    }
  }

  @Override
  public int getNextTownId() {
    // P3.5: Use cached value if available
    if (shouldRefreshMetadataCache()) {
      refreshMetadataCache();
    }

    String value = metadataCache.get("next_town_id");
    return value != null ? Integer.parseInt(value) : 1;
  }

  @Override
  public void updateNextTownId(int newId) {
    String upsertSQL =
        "INSERT INTO tan_metadata (meta_key, meta_value) VALUES ('next_town_id', ?) ON DUPLICATE KEY UPDATE meta_value = VALUES(meta_value)";
    try (Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement(upsertSQL)) {
      ps.setString(1, String.valueOf(newId));
      ps.executeUpdate();
      // Update cache immediately
      metadataCache.put("next_town_id", String.valueOf(newId));
    } catch (SQLException e) {
      TownsAndNations.getPlugin()
          .getLogger()
          .severe("Error updating next_town_id: " + e.getMessage());
    }
  }

  @Override
  public int getNextRegionId() {
    // P3.5: Use cached value if available
    if (shouldRefreshMetadataCache()) {
      refreshMetadataCache();
    }

    String value = metadataCache.get("next_region_id");
    return value != null ? Integer.parseInt(value) : 1;
  }

  @Override
  public void updateNextRegionId(int newId) {
    String upsertSQL =
        "INSERT INTO tan_metadata (meta_key, meta_value) VALUES ('next_region_id', ?) ON DUPLICATE KEY UPDATE meta_value = VALUES(meta_value)";
    try (Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement(upsertSQL)) {
      ps.setString(1, String.valueOf(newId));
      ps.executeUpdate();
      // Update cache immediately
      metadataCache.put("next_region_id", String.valueOf(newId));
    } catch (SQLException e) {
      TownsAndNations.getPlugin()
          .getLogger()
          .severe("Error updating next_region_id: " + e.getMessage());
    }
  }

  /**
   * Check if metadata cache needs to be refreshed
   *
   * @return true if cache is expired, false otherwise
   */
  private static boolean shouldRefreshMetadataCache() {
    return System.currentTimeMillis() - metadataCacheTime > METADATA_CACHE_TTL;
  }

  /** Refresh metadata cache from database P3.5: Load all metadata in one query */
  private void refreshMetadataCache() {
    String selectSQL = "SELECT meta_key, meta_value FROM tan_metadata";
    try (Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(selectSQL)) {

      while (rs.next()) {
        metadataCache.put(rs.getString("meta_key"), rs.getString("meta_value"));
      }
      metadataCacheTime = System.currentTimeMillis();
    } catch (SQLException e) {
      TownsAndNations.getPlugin()
          .getLogger()
          .warning("Error refreshing metadata cache: " + e.getMessage());
    }
  }

  /**
   * Close the HikariCP connection pool and clean up resources P3.2: Proper resource cleanup
   * implementation
   */
  @Override
  public void close() {
    // Shutdown batch executor first
    shutdownQueryBatcher();

    if (dataSource instanceof HikariDataSource) {
      HikariDataSource hikari = (HikariDataSource) dataSource;
      try {
        if (!hikari.isClosed()) {
          plugin.getLogger().info("[TaN] Closing MySQL connection pool...");
          hikari.close();
          plugin.getLogger().info("[TaN] MySQL connection pool closed successfully");
        }
      } catch (Exception e) {
        plugin.getLogger().severe("[TaN] Error closing MySQL connection pool: " + e.getMessage());
      }
    }
    // Clear metadata cache on shutdown
    metadataCache.clear();
  }

  /** Reconnect to the database if connection is lost P3.4: MySQL reconnection logic */
  public void reconnect() {
    plugin.getLogger().warning("[TaN] Attempting to reconnect to MySQL database...");
    try {
      // Close existing connection if any
      if (hikariDataSource != null && !hikariDataSource.isClosed()) {
        hikariDataSource.close();
      }
      // Reconnect
      connect();
      plugin.getLogger().info("[TaN] Successfully reconnected to MySQL database");
    } catch (SQLException e) {
      plugin.getLogger().severe("[TaN] Failed to reconnect to MySQL database: " + e.getMessage());
    }
  }

  /**
   * Execute a query with query limiter protection. Prevents connection pool saturation for
   * high-player servers.
   *
   * @param query The query operation to execute
   * @return Result from the query
   * @throws SQLException If the query fails or timeout occurs
   */
  protected <T> T executeWithLimit(QuerySupplier<T> query) throws SQLException {
    if (queryLimiter == null) {
      return query.execute();
    }

    if (!queryLimiter.acquirePermit()) {
      throw new SQLException("Query queue full - timeout after 5s");
    }

    try {
      return query.execute();
    } finally {
      queryLimiter.releasePermit();
    }
  }

  /** Functional interface for queries to support query limiting. */
  @FunctionalInterface
  protected interface QuerySupplier<T> {
    T execute() throws SQLException;
  }

  /** Get query limiter statistics (for monitoring). */
  public String getQueryLimiterStats() {
    return queryLimiter != null ? queryLimiter.getStats() : "Query limiter not initialized";
  }
}
