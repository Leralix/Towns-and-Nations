package org.leralix.tan.storage.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import org.leralix.tan.TownsAndNations;

public class SQLiteHandler extends DatabaseHandler {

  private final String databasePath;
  private HikariDataSource hikariDataSource;

  public SQLiteHandler(String databasePath) {
    this.databasePath = databasePath;
  }

  @Override
  public void connect() throws SQLException {
    File dbFile = new File(databasePath);

    if (!dbFile.exists()) {
      try {
        if (dbFile.getParentFile() != null && !dbFile.getParentFile().exists()) {
          dbFile.getParentFile().mkdirs();
        }
        if (dbFile.createNewFile()) {
          TownsAndNations.getPlugin().getLogger().info("SQLite database created");
        }
      } catch (IOException e) {
        throw new SQLException("Error while creating SQLite database", e);
      }
    }

    HikariConfig config = new HikariConfig();
    config.setJdbcUrl("jdbc:sqlite:" + databasePath + "?journal_mode=WAL");
    config.setPoolName("TownsAndNations-SQLite-Pool");

    config.setMaximumPoolSize(10);
    config.setMinimumIdle(2);
    config.setConnectionTimeout(120000);
    config.setIdleTimeout(600000);
    config.setMaxLifetime(1800000);
    config.setLeakDetectionThreshold(60000);

    config.addDataSourceProperty("cachePrepStmts", "true");
    config.addDataSourceProperty("prepStmtCacheSize", "250");
    config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

    this.hikariDataSource = new HikariDataSource(config);

    this.dataSource = hikariDataSource;
    createMetadataTable();
    initialize();
  }

  @Override
  public void createMetadataTable() {
    String createTableSQL =
        """
            CREATE TABLE IF NOT EXISTS tan_metadata (
                meta_key TEXT PRIMARY KEY,
                meta_value TEXT NOT NULL
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
    String selectSQL = "SELECT meta_value FROM tan_metadata WHERE meta_key = 'next_town_id'";
    try (Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement(selectSQL)) {
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return Integer.parseInt(rs.getString("meta_value"));
        }
      }
    } catch (SQLException | NumberFormatException e) {
    }
    return 1;
  }

  @Override
  public void updateNextTownId(int newId) {
    String upsertSQL =
        "INSERT OR REPLACE INTO tan_metadata (meta_key, meta_value) VALUES ('next_town_id', ?)";
    try (Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement(upsertSQL)) {
      ps.setString(1, String.valueOf(newId));
      ps.executeUpdate();
    } catch (SQLException e) {
      TownsAndNations.getPlugin()
          .getLogger()
          .severe("Error updating next_town_id: " + e.getMessage());
    }
  }

  @Override
  public int getNextRegionId() {
    String selectSQL = "SELECT meta_value FROM tan_metadata WHERE meta_key = 'next_region_id'";
    try (Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement(selectSQL)) {
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return Integer.parseInt(rs.getString("meta_value"));
        }
      }
    } catch (SQLException | NumberFormatException e) {
    }
    return 1;
  }

  @Override
  public void updateNextRegionId(int newId) {
    String upsertSQL =
        "INSERT OR REPLACE INTO tan_metadata (meta_key, meta_value) VALUES ('next_region_id', ?)";
    try (Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement(upsertSQL)) {
      ps.setString(1, String.valueOf(newId));
      ps.executeUpdate();
    } catch (SQLException e) {
      TownsAndNations.getPlugin()
          .getLogger()
          .severe("Error updating next_region_id: " + e.getMessage());
    }
  }

  @Override
  public void close() {
    if (hikariDataSource != null && !hikariDataSource.isClosed()) {
      TownsAndNations.getPlugin().getLogger().info("[TaN] Closing SQLite connection pool...");
      try {
        hikariDataSource.close();
        TownsAndNations.getPlugin()
            .getLogger()
            .info("[TaN] SQLite connection pool closed successfully");
      } catch (Exception e) {
        TownsAndNations.getPlugin()
            .getLogger()
            .severe("[TaN] Error closing SQLite connection pool: " + e.getMessage());
      }
    }
  }
}
