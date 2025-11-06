package org.leralix.tan.storage.stored;

import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.dataclass.territory.cosmetic.ICustomIcon;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.typeadapter.EnumMapDeserializer;
import org.leralix.tan.storage.typeadapter.IconAdapter;
import org.leralix.tan.utils.FoliaScheduler;
import org.leralix.tan.utils.file.FileUtil;

public class RegionDataStorage extends DatabaseStorage<RegionData> {

  private static final String TABLE_NAME = "tan_regions";
  private int nextID;
  private static RegionDataStorage instance;

  public static RegionDataStorage getInstance() {
    if (instance == null) instance = new RegionDataStorage();
    return instance;
  }

  private RegionDataStorage() {
    super(
        TABLE_NAME,
        RegionData.class,
        new GsonBuilder()
            .registerTypeAdapter(
                new TypeToken<Map<TownRelation, List<String>>>() {}.getType(),
                new EnumMapDeserializer<>(
                    TownRelation.class, new TypeToken<List<String>>() {}.getType()))
            .registerTypeAdapter(ICustomIcon.class, new IconAdapter())
            .setPrettyPrinting()
            .create());
    loadNextID();
  }

  @Override
  protected void createTable() {
    String createTableSQL =
        """
            CREATE TABLE IF NOT EXISTS %s (
                id VARCHAR(255) PRIMARY KEY,
                data TEXT NOT NULL
            )
        """
            .formatted(TABLE_NAME);

    try (Connection conn = getDatabase().getDataSource().getConnection();
        Statement stmt = conn.createStatement()) {
      stmt.execute(createTableSQL);

      // Migration: Add region_name column if it doesn't exist
      try (ResultSet rs = conn.getMetaData().getColumns(null, null, TABLE_NAME, "region_name")) {
        if (!rs.next()) {
          stmt.executeUpdate(
              "ALTER TABLE %s ADD COLUMN region_name VARCHAR(255) NULL".formatted(TABLE_NAME));
          TownsAndNations.getPlugin().getLogger().info("Added region_name column to " + TABLE_NAME);
        }
      }

    } catch (SQLException e) {
      TownsAndNations.getPlugin()
          .getLogger()
          .severe("Error creating table " + TABLE_NAME + ": " + e.getMessage());
    }
  }

  @Override
  protected void createIndexes() {
    // PERFORMANCE FIX: Add index for frequently queried region_name column
    String createNameIndexSQL =
        "CREATE INDEX IF NOT EXISTS idx_region_name ON " + TABLE_NAME + " (region_name)";

    try (Connection conn = getDatabase().getDataSource().getConnection();
        Statement stmt = conn.createStatement()) {
      stmt.execute(createNameIndexSQL);
      TownsAndNations.getPlugin()
          .getLogger()
          .info("Created index idx_region_name on " + TABLE_NAME);
    } catch (SQLException e) {
      TownsAndNations.getPlugin()
          .getLogger()
          .warning("Error creating indexes for " + TABLE_NAME + ": " + e.getMessage());
    }
  }

  @Override
  public void put(String id, RegionData obj) {
    if (id == null || obj == null) {
      return;
    }

    String jsonData = gson.toJson(obj, typeToken);
    String upsertSQL;
    if (getDatabase().isMySQL()) {
      upsertSQL =
          "INSERT INTO "
              + tableName
              + " (id, region_name, data) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE region_name = VALUES(region_name), data = VALUES(data)";
    } else {
      upsertSQL =
          "INSERT OR REPLACE INTO " + tableName + " (id, region_name, data) VALUES (?, ?, ?)";
    }

    FoliaScheduler.runTaskAsynchronously(
        TownsAndNations.getPlugin(),
        () -> {
          try (Connection conn = getDatabase().getDataSource().getConnection();
              PreparedStatement ps = conn.prepareStatement(upsertSQL)) {

            ps.setString(1, id);
            ps.setString(2, obj.getName()); // Set region_name
            ps.setString(3, jsonData);
            ps.executeUpdate();

            // Update cache
            if (cacheEnabled && cache != null) {
              synchronized (cache) {
                cache.put(id, obj);
              }
            }

          } catch (SQLException e) {
            TownsAndNations.getPlugin()
                .getLogger()
                .severe(
                    "Error storing "
                        + typeClass.getSimpleName()
                        + " with ID "
                        + id
                        + ": "
                        + e.getMessage());
          }
        });
  }

  private void loadNextID() {
    nextID = getDatabase().getNextRegionId();
  }

  public CompletableFuture<RegionData> createNewRegion(String name, TownData capital) {

    ITanPlayer newLeader = capital.getLeaderData();

    String regionID = generateNextID();

    RegionData newRegion = new RegionData(regionID, name, newLeader);
    put(regionID, newRegion);
    capital.setOverlord(newRegion);

    FileUtil.addLineToHistory(Lang.REGION_CREATED_NEWSLETTER.get(newLeader.getNameStored(), name));
    return CompletableFuture.completedFuture(newRegion);
  }

  private @NotNull String generateNextID() {
    String regionID = "R" + nextID;
    nextID++;
    getDatabase().updateNextRegionId(nextID);
    return regionID;
  }

  public CompletableFuture<RegionData> get(Player player) {
    return PlayerDataStorage.getInstance().get(player).thenCompose(this::get);
  }

  public CompletableFuture<RegionData> get(ITanPlayer tanPlayer) {
    return TownDataStorage.getInstance()
        .get(tanPlayer)
        .thenCompose(
            town -> {
              if (town == null) return CompletableFuture.completedFuture(null);
              return CompletableFuture.completedFuture(town.getRegionSync());
            });
  }

  public void deleteRegion(RegionData region) {
    delete(region.getID());
  }

  public boolean isNameUsed(String name) {
    if (name == null) {
      return false;
    }

    // Optimized: scan JSON data for name instead of deserializing all objects
    String selectSQL =
        "SELECT 1 FROM " + TABLE_NAME + " WHERE json_extract(data, '$.name') = ? LIMIT 1";

    try (Connection conn = getDatabase().getDataSource().getConnection();
        PreparedStatement ps = conn.prepareStatement(selectSQL)) {

      ps.setString(1, name);

      try (ResultSet rs = ps.executeQuery()) {
        return rs.next();
      }
    } catch (SQLException e) {
      // Fallback to the old method if json_extract is not supported
      TownsAndNations.getPlugin()
          .getLogger()
          .warning("json_extract not supported, falling back to full scan: " + e.getMessage());

      for (RegionData region : getAll().values()) {
        if (name.equals(region.getName())) return true;
      }
    }

    return false;
  }

  public RegionData getSync(Player player) {
    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getSync(player);
    if (tanPlayer == null) return null;
    return getSync(tanPlayer);
  }

  @Override
  public void reset() {
    instance = null;
  }

  /**
   * Synchronous get method for backward compatibility WARNING: This blocks the current thread. Use
   * get() with thenAccept() for async operations.
   *
   * @param id The ID of the region
   * @return The region data, or null if not found
   */
  public RegionData getSync(String id) {
    // PERFORMANCE FIX: Use cache-only access to prevent server freezing
    if (cacheEnabled && cache != null) {
      RegionData cached = cache.get(id);
      if (cached != null) {
        return cached;
      }
    }

    // Not in cache - trigger async load in background but return immediately
    get(id)
        .thenAccept(
            region -> {
              if (region != null && cacheEnabled && cache != null) {
                cache.put(id, region);
              }
            });

    return null;
  }

  public RegionData getSync(ITanPlayer tanPlayer) {
    // NOTE: This still uses blocking call via get(tanPlayer).join()
    // but at least getSync(String) is now cache-only
    try {
      return get(tanPlayer).join();
    } catch (Exception e) {
      TownsAndNations.getPlugin()
          .getLogger()
          .warning("Error getting region data synchronously: " + e.getMessage());
      return null;
    }
  }
}
