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

  private static final ConcurrentMap<String, String> metadataCache = new ConcurrentHashMap<>();
  private static final long METADATA_CACHE_TTL = 300_000L;
  private static volatile long metadataCacheTime = 0;

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

    String sslParams = "?useSSL=" + sslEnabled;
    if (sslEnabled) {
      sslParams += "&requireSSL=" + sslRequired;
      sslParams += "&verifyServerCertificate=" + verifyServerCert;
    }

    config.setJdbcUrl(
        String.format("jdbc:mysql://%s:%s/%s%s", host, port, databaseName, sslParams));
    config.setUsername(user);
    config.setPassword(password);

    config.addDataSourceProperty("cachePrepStmts", "true");
    config.addDataSourceProperty("prepStmtCacheSize", "500");
    config.addDataSourceProperty("prepStmtCacheSqlLimit", "4096");
    config.addDataSourceProperty("useServerPrepStmts", "true");

    config.addDataSourceProperty("maintainTimeStats", "false");
    config.addDataSourceProperty("alwaysSendSetIsolation", "false");
    config.addDataSourceProperty("enableQueryTimeouts", "false");

    config.addDataSourceProperty("tcpKeepAlive", "true");
    config.addDataSourceProperty("tcpNoDelay", "true");

    config.setPoolName("TownsAndNations-MySql-Pool");

    config.setMaximumPoolSize(plugin.getConfig().getInt("database.pool-size", 30));
    config.setMinimumIdle(plugin.getConfig().getInt("database.min-idle", 5));
    config.setConnectionTimeout(plugin.getConfig().getLong("database.connection-timeout", 30000L));
    config.setIdleTimeout(plugin.getConfig().getLong("database.idle-timeout", 600000L));
    config.setMaxLifetime(plugin.getConfig().getLong("database.max-lifetime", 1800000L));

    config.setLeakDetectionThreshold(
        plugin.getConfig().getLong("database.leak-detection-threshold", 60000L));

    plugin.getLogger().info("[TaN-MySQL] Creating HikariCP connection pool...");
    this.dataSource = new HikariDataSource(config);
    plugin.getLogger().info("[TaN-MySQL] HikariCP pool created successfully");
    plugin
        .getLogger()
        .info(
            "[TaN-MySQL] Pool size: "
                + config.getMaximumPoolSize()
                + ", Min idle: "
                + config.getMinimumIdle());

    this.queryLimiter = new QueryLimiter(100, 5000);
    plugin.getLogger().info("[TaN-MySQL] Query limiter initialized: max 100 concurrent queries");

    initializeQueryBatcher(50, 100);
    plugin.getLogger().info("[TaN-MySQL] Query batch executor initialized");

    initializeBatchWriter(50, 1000);
    plugin.getLogger().info("[TaN-MySQL] Batch write optimizer initialized");

    plugin.getLogger().info("[TaN-MySQL] Creating metadata table...");
    createMetadataTable();
    plugin.getLogger().info("[TaN-MySQL] Initializing database structures...");
    initialize();
    plugin.getLogger().info("[TaN-MySQL] MySQL connection fully initialized and ready");
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
      metadataCache.put("next_town_id", String.valueOf(newId));
    } catch (SQLException e) {
      TownsAndNations.getPlugin()
          .getLogger()
          .severe("Error updating next_town_id: " + e.getMessage());
    }
  }

  @Override
  public int getNextRegionId() {
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
      metadataCache.put("next_region_id", String.valueOf(newId));
    } catch (SQLException e) {
      TownsAndNations.getPlugin()
          .getLogger()
          .severe("Error updating next_region_id: " + e.getMessage());
    }
  }

  private static boolean shouldRefreshMetadataCache() {
    return System.currentTimeMillis() - metadataCacheTime > METADATA_CACHE_TTL;
  }

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

  @Override
  public void close() {
    shutdownBatchWriter();

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
    metadataCache.clear();
  }

  public void reconnect() {
    plugin.getLogger().warning("[TaN] Attempting to reconnect to MySQL database...");
    try {
      if (hikariDataSource != null && !hikariDataSource.isClosed()) {
        hikariDataSource.close();
      }
      connect();
      plugin.getLogger().info("[TaN] Successfully reconnected to MySQL database");
    } catch (SQLException e) {
      plugin.getLogger().severe("[TaN] Failed to reconnect to MySQL database: " + e.getMessage());
    }
  }

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

  @FunctionalInterface
  protected interface QuerySupplier<T> {
    T execute() throws SQLException;
  }

  public String getQueryLimiterStats() {
    return queryLimiter != null ? queryLimiter.getStats() : "Query limiter not initialized";
  }
}
