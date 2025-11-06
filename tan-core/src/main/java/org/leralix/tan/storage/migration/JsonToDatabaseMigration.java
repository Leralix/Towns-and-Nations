package org.leralix.tan.storage.migration;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.leralix.tan.TownsAndNations;

/** Utility class to migrate data from JSON files to database */
public class JsonToDatabaseMigration {

  private static final String STORAGE_FOLDER = "storage/json";

  /**
   * Migrate a single JSON file to database table
   *
   * @param jsonFileName The JSON file name (e.g., "TAN - Players.json")
   * @param tableName The target database table name
   * @return Number of records migrated
   */
  public static int migrateJsonToDatabase(String jsonFileName, String tableName) {
    File pluginFolder = TownsAndNations.getPlugin().getDataFolder();
    File jsonFile = new File(new File(pluginFolder, STORAGE_FOLDER), jsonFileName);

    if (!jsonFile.exists()) {
      TownsAndNations.getPlugin()
          .getLogger()
          .info("No JSON file found at " + jsonFile.getAbsolutePath() + ", skipping migration");
      return 0;
    }

    int migrated = 0;
    Gson gson = new Gson();

    Connection conn = null;
    try (FileReader reader = new FileReader(jsonFile)) {
      JsonObject rootObject = JsonParser.parseReader(reader).getAsJsonObject();
      Map<String, String> dataMap = new HashMap<>();

      // Parse JSON into id -> jsonData map
      for (Map.Entry<String, com.google.gson.JsonElement> entry : rootObject.entrySet()) {
        String key = entry.getKey();
        String jsonData = gson.toJson(entry.getValue());
        dataMap.put(key, jsonData);
      }

      // Batch insert into database
      String insertSQL = TownsAndNations.getPlugin().getDatabaseHandler().getUpsertSQL(tableName);

      try {
        conn = TownsAndNations.getPlugin().getDatabaseHandler().getDataSource().getConnection();
        conn.setAutoCommit(false);

        try (PreparedStatement ps = conn.prepareStatement(insertSQL)) {
          for (Map.Entry<String, String> entry : dataMap.entrySet()) {
            ps.setString(1, entry.getKey());
            ps.setString(2, entry.getValue());
            ps.addBatch();
            migrated++;
          }

          ps.executeBatch();
          conn.commit();

          TownsAndNations.getPlugin()
              .getLogger()
              .info(
                  "Successfully migrated "
                      + migrated
                      + " records from "
                      + jsonFileName
                      + " to "
                      + tableName);

        } catch (SQLException e) {
          try {
            if (conn != null) {
              conn.rollback();
            }
          } catch (SQLException rollbackEx) {
            TownsAndNations.getPlugin()
                .getLogger()
                .severe("Error rolling back migration: " + rollbackEx.getMessage());
          }
          throw e;
        } finally {
          if (conn != null) {
            conn.setAutoCommit(true);
          }
        }

      } catch (SQLException e) {
        TownsAndNations.getPlugin()
            .getLogger()
            .severe("Error migrating " + jsonFileName + " to database: " + e.getMessage());
        e.printStackTrace();
      } finally {
        if (conn != null) {
          try {
            conn.close();
          } catch (SQLException e) {
            TownsAndNations.getPlugin()
                .getLogger()
                .warning("Error closing connection: " + e.getMessage());
          }
        }
      }

    } catch (IOException e) {
      TownsAndNations.getPlugin()
          .getLogger()
          .severe("Error reading JSON file " + jsonFileName + ": " + e.getMessage());
      e.printStackTrace();
    }

    return migrated;
  }

  /** Migrate all standard JSON files to database */
  public static void migrateAllJsonToDatabase() {
    TownsAndNations.getPlugin().getLogger().info("Starting JSON to Database migration...");

    int totalMigrated = 0;

    totalMigrated += migrateJsonToDatabase("TAN - Players.json", "tan_players");
    totalMigrated += migrateJsonToDatabase("TAN - Towns.json", "tan_towns");
    totalMigrated += migrateJsonToDatabase("TAN - Regions.json", "tan_regions");
    totalMigrated += migrateJsonToDatabase("TAN - Claimed Chunks.json", "tan_claimed_chunks");
    totalMigrated += migrateJsonToDatabase("TAN - Landmarks.json", "tan_landmarks");
    totalMigrated += migrateJsonToDatabase("Wars.json", "tan_wars");
    totalMigrated += migrateJsonToDatabase("TAN - Planned_wars.json", "tan_planned_attacks");
    totalMigrated += migrateJsonToDatabase("TAN - Truce.json", "tan_truces");
    totalMigrated += migrateJsonToDatabase("Premium accounts.json", "tan_premium_accounts");

    TownsAndNations.getPlugin()
        .getLogger()
        .info("Migration complete! Total records migrated: " + totalMigrated);
  }

  /** Backup JSON files after successful migration */
  public static void backupJsonFiles() {
    File pluginFolder = TownsAndNations.getPlugin().getDataFolder();
    File jsonFolder = new File(pluginFolder, STORAGE_FOLDER);
    File backupFolder = new File(pluginFolder, "storage/json_backup_" + System.currentTimeMillis());

    if (jsonFolder.exists() && jsonFolder.isDirectory()) {
      if (backupFolder.mkdirs()) {
        File[] files = jsonFolder.listFiles((dir, name) -> name.endsWith(".json"));
        if (files != null) {
          for (File file : files) {
            File backup = new File(backupFolder, file.getName());
            if (file.renameTo(backup)) {
              TownsAndNations.getPlugin()
                  .getLogger()
                  .info("Backed up " + file.getName() + " to " + backupFolder.getName());
            }
          }
        }
      }
    }
  }
}
