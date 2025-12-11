package org.leralix.tan.storage;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.storage.database.DatabaseHandler;

public class TableInitializer {

  private final DatabaseHandler database;

  public TableInitializer(DatabaseHandler database) {
    this.database = database;
  }

  public void initializeAllTables() {
    TownsAndNations.getPlugin()
        .getLogger()
        .info("[TaN-TableInit] Starting forced table initialization...");

    try {
      createTownTables();
      createPlayerTables();
      createRegionTables();
      createChunkTables();
      createWarTables();
      createLandmarkTables();
      createNewsletterTables();
      createMiscTables();

      TownsAndNations.getPlugin()
          .getLogger()
          .info("[TaN-TableInit] ✓ All tables initialized successfully!");
    } catch (Exception e) {
      TownsAndNations.getPlugin()
          .getLogger()
          .severe(
              "[TaN-TableInit] ✗ CRITICAL ERROR during table initialization: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private void createTownTables() throws SQLException {
    String tableName = "tan_towns";
    TownsAndNations.getPlugin().getLogger().info("[TaN-TableInit] Creating table: " + tableName);

    String createSQL;
    if (database.isMySQL()) {
      createSQL =
          """
        CREATE TABLE IF NOT EXISTS %s (
            id VARCHAR(255) PRIMARY KEY,
            data MEDIUMTEXT NOT NULL,
            town_name VARCHAR(255),
            creator_uuid VARCHAR(36),
            creator_name VARCHAR(255),
            creation_date BIGINT,
            INDEX idx_town_name (town_name),
            INDEX idx_creator_uuid (creator_uuid),
            INDEX idx_creation_date (creation_date)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
    """
              .formatted(tableName);
    } else {
      createSQL =
          """
        CREATE TABLE IF NOT EXISTS %s (
            id VARCHAR(255) PRIMARY KEY,
            data TEXT NOT NULL,
            town_name VARCHAR(255),
            creator_uuid VARCHAR(255),
            creator_name VARCHAR(255),
            creation_date BIGINT
        )
    """
              .formatted(tableName);
    }

    executeSQL(createSQL, tableName);
  }

  private void createPlayerTables() throws SQLException {
    String tableName = "tan_players";
    TownsAndNations.getPlugin().getLogger().info("[TaN-TableInit] Creating table: " + tableName);

    String createSQL;
    if (database.isMySQL()) {
      createSQL =
          """
        CREATE TABLE IF NOT EXISTS %s (
            id VARCHAR(36) PRIMARY KEY,
            data MEDIUMTEXT NOT NULL,
            player_name VARCHAR(255),
            last_login BIGINT,
            INDEX idx_player_name (player_name),
            INDEX idx_last_login (last_login)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
    """
              .formatted(tableName);
    } else {
      createSQL =
          """
        CREATE TABLE IF NOT EXISTS %s (
            id VARCHAR(255) PRIMARY KEY,
            data TEXT NOT NULL,
            player_name VARCHAR(255),
            last_login BIGINT
        )
    """
              .formatted(tableName);
    }

    executeSQL(createSQL, tableName);
  }

  private void createRegionTables() throws SQLException {
    String tableName = "tan_regions";
    TownsAndNations.getPlugin().getLogger().info("[TaN-TableInit] Creating table: " + tableName);

    String createSQL;
    if (database.isMySQL()) {
      createSQL =
          """
        CREATE TABLE IF NOT EXISTS %s (
            id VARCHAR(255) PRIMARY KEY,
            data MEDIUMTEXT NOT NULL,
            region_name VARCHAR(255),
            capital_town_id VARCHAR(255),
            INDEX idx_region_name (region_name),
            INDEX idx_capital_town_id (capital_town_id)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
    """
              .formatted(tableName);
    } else {
      createSQL =
          """
        CREATE TABLE IF NOT EXISTS %s (
            id VARCHAR(255) PRIMARY KEY,
            data TEXT NOT NULL,
            region_name VARCHAR(255),
            capital_town_id VARCHAR(255)
        )
    """
              .formatted(tableName);
    }

    executeSQL(createSQL, tableName);
  }

  private void createChunkTables() throws SQLException {
    String tableName = "tan_chunks";
    TownsAndNations.getPlugin().getLogger().info("[TaN-TableInit] Creating table: " + tableName);

    String createSQL;
    if (database.isMySQL()) {
      createSQL =
          """
        CREATE TABLE IF NOT EXISTS %s (
            id VARCHAR(255) PRIMARY KEY,
            data MEDIUMTEXT NOT NULL,
            world VARCHAR(255),
            x INT,
            z INT,
            owner_id VARCHAR(255),
            INDEX idx_chunk_location (world, x, z),
            INDEX idx_owner_id (owner_id)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
    """
              .formatted(tableName);
      executeSQL(createSQL, tableName);
    } else {
      createSQL =
          """
        CREATE TABLE IF NOT EXISTS %s (
            id VARCHAR(255) PRIMARY KEY,
            data TEXT NOT NULL,
            world VARCHAR(255),
            x INT,
            z INT,
            owner_id VARCHAR(255)
        )
    """
              .formatted(tableName);
      executeSQL(createSQL, tableName);

      String indexSQL =
          "CREATE INDEX IF NOT EXISTS idx_chunk_location ON " + tableName + " (world, x, z)";
      executeSQL(indexSQL, tableName + " location index");

      String ownerIndexSQL =
          "CREATE INDEX IF NOT EXISTS idx_owner_id ON " + tableName + " (owner_id)";
      executeSQL(ownerIndexSQL, tableName + " owner index");
    }
  }

  private void createWarTables() throws SQLException {
    String tableName = "tan_wars";
    TownsAndNations.getPlugin().getLogger().info("[TaN-TableInit] Creating table: " + tableName);

    String createSQL;
    if (database.isMySQL()) {
      createSQL =
          """
        CREATE TABLE IF NOT EXISTS %s (
            id VARCHAR(255) PRIMARY KEY,
            data MEDIUMTEXT NOT NULL
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
    """
              .formatted(tableName);
    } else {
      createSQL =
          """
        CREATE TABLE IF NOT EXISTS %s (
            id VARCHAR(255) PRIMARY KEY,
            data TEXT NOT NULL
        )
    """
              .formatted(tableName);
    }

    executeSQL(createSQL, tableName);
  }

  private void createLandmarkTables() throws SQLException {
    String tableName = "tan_landmarks";
    TownsAndNations.getPlugin().getLogger().info("[TaN-TableInit] Creating table: " + tableName);

    String createSQL;
    if (database.isMySQL()) {
      createSQL =
          """
        CREATE TABLE IF NOT EXISTS %s (
            id VARCHAR(255) PRIMARY KEY,
            data MEDIUMTEXT NOT NULL
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
    """
              .formatted(tableName);
    } else {
      createSQL =
          """
        CREATE TABLE IF NOT EXISTS %s (
            id VARCHAR(255) PRIMARY KEY,
            data TEXT NOT NULL
        )
    """
              .formatted(tableName);
    }

    executeSQL(createSQL, tableName);
  }

  private void createNewsletterTables() throws SQLException {
    String tableName = "tan_newsletter";
    TownsAndNations.getPlugin().getLogger().info("[TaN-TableInit] Creating table: " + tableName);

    String createSQL;
    if (database.isMySQL()) {
      createSQL =
          """
        CREATE TABLE IF NOT EXISTS %s (
            id VARCHAR(255) PRIMARY KEY,
            data MEDIUMTEXT NOT NULL,
            date BIGINT,
            type VARCHAR(100),
            INDEX idx_date (date),
            INDEX idx_type (type)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
    """
              .formatted(tableName);
    } else {
      createSQL =
          """
        CREATE TABLE IF NOT EXISTS %s (
            id VARCHAR(255) PRIMARY KEY,
            data TEXT NOT NULL,
            date BIGINT,
            type VARCHAR(100)
        )
    """
              .formatted(tableName);
    }

    executeSQL(createSQL, tableName);
  }

  private void createMiscTables() throws SQLException {
    String createSQL;

    String plannedAttacksTable = "tan_plannedattacks";
    TownsAndNations.getPlugin()
        .getLogger()
        .info("[TaN-TableInit] Creating table: " + plannedAttacksTable);

    if (database.isMySQL()) {
      createSQL =
          """
        CREATE TABLE IF NOT EXISTS %s (
            id VARCHAR(255) PRIMARY KEY,
            data MEDIUMTEXT NOT NULL
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
    """
              .formatted(plannedAttacksTable);
    } else {
      createSQL =
          """
        CREATE TABLE IF NOT EXISTS %s (
            id VARCHAR(255) PRIMARY KEY,
            data TEXT NOT NULL
        )
    """
              .formatted(plannedAttacksTable);
    }
    executeSQL(createSQL, plannedAttacksTable);

    String trucesTable = "tan_truces";
    TownsAndNations.getPlugin().getLogger().info("[TaN-TableInit] Creating table: " + trucesTable);

    if (database.isMySQL()) {
      createSQL =
          """
        CREATE TABLE IF NOT EXISTS %s (
            id VARCHAR(255) PRIMARY KEY,
            data MEDIUMTEXT NOT NULL
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
    """
              .formatted(trucesTable);
    } else {
      createSQL =
          """
        CREATE TABLE IF NOT EXISTS %s (
            id VARCHAR(255) PRIMARY KEY,
            data TEXT NOT NULL
        )
    """
              .formatted(trucesTable);
    }
    executeSQL(createSQL, trucesTable);

    String premiumTable = "tan_premium";
    TownsAndNations.getPlugin().getLogger().info("[TaN-TableInit] Creating table: " + premiumTable);

    if (database.isMySQL()) {
      createSQL =
          """
        CREATE TABLE IF NOT EXISTS %s (
            id VARCHAR(255) PRIMARY KEY,
            data MEDIUMTEXT NOT NULL
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
    """
              .formatted(premiumTable);
    } else {
      createSQL =
          """
        CREATE TABLE IF NOT EXISTS %s (
            id VARCHAR(255) PRIMARY KEY,
            data TEXT NOT NULL
        )
    """
              .formatted(premiumTable);
    }
    executeSQL(createSQL, premiumTable);
  }

  private void executeSQL(String sql, String description) throws SQLException {
    try (Connection conn = database.getDataSource().getConnection();
        Statement stmt = conn.createStatement()) {
      stmt.execute(sql);
      TownsAndNations.getPlugin().getLogger().info("[TaN-TableInit] ✓ " + description + " OK");
    } catch (SQLException e) {
      TownsAndNations.getPlugin().getLogger().severe("[TaN-TableInit] ✗ Failed: " + description);
      throw e;
    }
  }
}
