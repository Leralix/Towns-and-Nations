package org.leralix.tan.tasks;

import org.bukkit.Bukkit;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.storage.stored.*;

public class SaveStats {

  private static final long SAVE_INTERVAL_TICKS = 6000L;

  private SaveStats() {
    throw new IllegalStateException("Utility class");
  }

  public static void startSchedule() {
    TownsAndNations plugin = TownsAndNations.getPlugin();

    plugin.getLogger().info("[TaN-AutoSave] Starting auto-save task every 5 minutes...");

    long delayMs = SAVE_INTERVAL_TICKS * 50;
    Bukkit.getAsyncScheduler()
        .runAtFixedRate(
            plugin, (t) -> saveAll(), delayMs, delayMs, java.util.concurrent.TimeUnit.MILLISECONDS);

    plugin.getLogger().info("[TaN-AutoSave] Auto-save task started successfully");
  }

  public static void saveAll() {
    TownsAndNations plugin = TownsAndNations.getPlugin();
    long startTime = System.currentTimeMillis();

    plugin.getLogger().info("[TaN-AutoSave] Starting automatic save of all data...");

    try {
      int townCount = saveTowns();

      int regionCount = saveRegions();

      int playerCount = savePlayers();

      long duration = System.currentTimeMillis() - startTime;

      plugin
          .getLogger()
          .info(
              String.format(
                  "[TaN-AutoSave] ✓ Saved %d towns, %d regions, %d players in %dms",
                  townCount, regionCount, playerCount, duration));
    } catch (Exception e) {
      plugin.getLogger().severe("[TaN-AutoSave] ✗ Error during auto-save: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private static int saveTowns() {
    try {
      TownDataStorage storage = TownDataStorage.getInstance();
      java.util.Map<String, TownData> allTowns = storage.getAllSync();

      for (java.util.Map.Entry<String, TownData> entry : allTowns.entrySet()) {
        storage.putAsync(entry.getKey(), entry.getValue());
      }

      return allTowns.size();
    } catch (Exception e) {
      TownsAndNations.getPlugin()
          .getLogger()
          .warning("[TaN-AutoSave] Error saving towns: " + e.getMessage());
      return 0;
    }
  }

  private static int saveRegions() {
    try {
      RegionDataStorage storage = RegionDataStorage.getInstance();
      java.util.Map<String, RegionData> allRegions = storage.getAllSync();

      for (java.util.Map.Entry<String, RegionData> entry : allRegions.entrySet()) {
        storage.putAsync(entry.getKey(), entry.getValue());
      }

      return allRegions.size();
    } catch (Exception e) {
      TownsAndNations.getPlugin()
          .getLogger()
          .warning("[TaN-AutoSave] Error saving regions: " + e.getMessage());
      return 0;
    }
  }

  private static int savePlayers() {
    try {
      PlayerDataStorage storage = PlayerDataStorage.getInstance();
      int saved = 0;

      for (org.bukkit.entity.Player player : Bukkit.getOnlinePlayers()) {
        try {
          ITanPlayer tanPlayer = storage.get(player.getUniqueId().toString()).join();
          if (tanPlayer != null) {
            storage.putAsync(player.getUniqueId().toString(), tanPlayer);
            saved++;
          }
        } catch (Exception e) {
        }
      }

      return saved;
    } catch (Exception e) {
      TownsAndNations.getPlugin()
          .getLogger()
          .warning("[TaN-AutoSave] Error saving players: " + e.getMessage());
      return 0;
    }
  }
}
