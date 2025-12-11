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
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.property.AbstractOwner;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.dataclass.territory.cosmetic.ICustomIcon;
import org.leralix.tan.dataclass.territory.permission.RelationPermission;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.enums.permissions.ChunkPermissionType;
import org.leralix.tan.storage.typeadapter.EnumMapDeserializer;
import org.leralix.tan.storage.typeadapter.EnumMapKeyValueDeserializer;
import org.leralix.tan.storage.typeadapter.IconAdapter;
import org.leralix.tan.storage.typeadapter.OwnerDeserializer;
import org.leralix.tan.utils.FoliaScheduler;

public class TownDataStorage extends DatabaseStorage<TownData> {

  private static final String TABLE_NAME = "tan_towns";
  private static TownDataStorage instance;

  private int newTownId;

  private final Set<String> pendingSaves = ConcurrentHashMap.newKeySet();
  private final Map<String, Long> lastSaveTime = new ConcurrentHashMap<>();
  private static final long SAVE_DEBOUNCE_MS = 500;

  private TownDataStorage() {
    super(
        TABLE_NAME,
        TownData.class,
        new GsonBuilder()
            .registerTypeAdapter(
                new TypeToken<Map<ChunkPermissionType, RelationPermission>>() {}.getType(),
                new EnumMapKeyValueDeserializer<>(
                    ChunkPermissionType.class, RelationPermission.class))
            .registerTypeAdapter(
                new TypeToken<Map<TownRelation, List<String>>>() {}.getType(),
                new EnumMapDeserializer<>(
                    TownRelation.class, new TypeToken<List<String>>() {}.getType()))
            .registerTypeAdapter(
                new TypeToken<List<RelationPermission>>() {}.getType(),
                new EnumMapDeserializer<>(
                    RelationPermission.class, new TypeToken<List<String>>() {}.getType()))
            .registerTypeAdapter(AbstractOwner.class, new OwnerDeserializer())
            .registerTypeAdapter(ICustomIcon.class, new IconAdapter())
            .setPrettyPrinting()
            .create());
    loadNextTownId();
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
      TownsAndNations.getPlugin().getLogger().info("[TaN-DB] Creating table: " + TABLE_NAME);
      stmt.execute(createTableSQL);
      TownsAndNations.getPlugin()
          .getLogger()
          .info("[TaN-DB] Table " + TABLE_NAME + " created/verified successfully");

      try (ResultSet rs = conn.getMetaData().getColumns(null, null, TABLE_NAME, "town_name")) {
        if (!rs.next()) {
          stmt.executeUpdate(
              "ALTER TABLE %s ADD COLUMN town_name VARCHAR(255) NULL".formatted(TABLE_NAME));
          TownsAndNations.getPlugin().getLogger().info("Added town_name column to " + TABLE_NAME);
        }
      }

      try (ResultSet rs = conn.getMetaData().getColumns(null, null, TABLE_NAME, "creator_uuid")) {
        if (!rs.next()) {
          stmt.executeUpdate(
              "ALTER TABLE %s ADD COLUMN creator_uuid VARCHAR(255) NULL".formatted(TABLE_NAME));
          TownsAndNations.getPlugin()
              .getLogger()
              .info("Added creator_uuid column to " + TABLE_NAME);
        }
      }

      try (ResultSet rs = conn.getMetaData().getColumns(null, null, TABLE_NAME, "creator_name")) {
        if (!rs.next()) {
          stmt.executeUpdate(
              "ALTER TABLE %s ADD COLUMN creator_name VARCHAR(255) NULL".formatted(TABLE_NAME));
          TownsAndNations.getPlugin()
              .getLogger()
              .info("Added creator_name column to " + TABLE_NAME);
        }
      }

      try (ResultSet rs = conn.getMetaData().getColumns(null, null, TABLE_NAME, "creation_date")) {
        if (!rs.next()) {
          if (getDatabase().isMySQL()) {
            stmt.executeUpdate(
                "ALTER TABLE %s ADD COLUMN creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                    .formatted(TABLE_NAME));
          } else {
            stmt.executeUpdate(
                "ALTER TABLE %s ADD COLUMN creation_date DATETIME DEFAULT CURRENT_TIMESTAMP"
                    .formatted(TABLE_NAME));
          }
          TownsAndNations.getPlugin()
              .getLogger()
              .info("Added creation_date column to " + TABLE_NAME);
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
    String createNameIndexSQL =
        "CREATE INDEX IF NOT EXISTS idx_town_name ON " + TABLE_NAME + " (town_name)";
    String createCreatorUuidIndexSQL =
        "CREATE INDEX IF NOT EXISTS idx_town_creator_uuid ON " + TABLE_NAME + " (creator_uuid)";
    String createCreationDateIndexSQL =
        "CREATE INDEX IF NOT EXISTS idx_town_creation_date ON " + TABLE_NAME + " (creation_date)";

    try (Connection conn = getDatabase().getDataSource().getConnection();
        Statement stmt = conn.createStatement()) {
      stmt.execute(createNameIndexSQL);
      stmt.execute(createCreatorUuidIndexSQL);
      stmt.execute(createCreationDateIndexSQL);
      TownsAndNations.getPlugin().getLogger().info("Created indexes on " + TABLE_NAME);
    } catch (SQLException e) {
      TownsAndNations.getPlugin()
          .getLogger()
          .warning("Error creating indexes for " + TABLE_NAME + ": " + e.getMessage());
    }
  }

  @Override
  public void put(String id, TownData obj) {
    if (id == null || obj == null) {
      return;
    }

    // ✅ SYNC-FIX: Debounce logic preserved, but delegate to putWithInvalidation
    long now = System.currentTimeMillis();
    Long lastSave = lastSaveTime.get(id);
    if (lastSave != null && (now - lastSave) < SAVE_DEBOUNCE_MS) {
      if (!pendingSaves.contains(id)) {
        pendingSaves.add(id);
        FoliaScheduler.runTaskLaterAsynchronously(
            TownsAndNations.getPlugin(),
            () -> {
              pendingSaves.remove(id);
              put(id, obj); // Recursive call after debounce
            },
            SAVE_DEBOUNCE_MS / 50);
      }
      return;
    }

    if (pendingSaves.contains(id)) {
      return;
    }

    pendingSaves.add(id);
    lastSaveTime.put(id, now);

    // ✅ SYNC-FIX: Use putWithInvalidation for cache invalidation + broadcast
    putWithInvalidation(id, obj)
        .thenRun(() -> pendingSaves.remove(id))
        .exceptionally(throwable -> {
          pendingSaves.remove(id);
          TownsAndNations.getPlugin()
              .getLogger()
              .severe("Error in TownDataStorage.put() delegation: " + throwable.getMessage());
          return null;
        });
  }

  private void loadNextTownId() {
    newTownId = getDatabase().getNextTownId();
  }

  @Override
  public void reset() {
    instance = null;
  }

  public static TownDataStorage getInstance() {
    if (instance == null) instance = new TownDataStorage();
    return instance;
  }

  public CompletableFuture<TownData> newTown(String townName, ITanPlayer tanPlayer) {
    String townId = getNextTownID();
    TownData newTown = new TownData(townId, townName, tanPlayer);

    putWithInvalidation(townId, newTown).join(); // ✅ SYNC-FIX: Use putWithInvalidation

    try {
      org.leralix.tan.redis.RedisSyncManager syncManager =
          TownsAndNations.getPlugin().getRedisSyncManager();
      if (syncManager != null) {
        com.google.gson.JsonObject payload = new com.google.gson.JsonObject();
        payload.addProperty("territoryId", townId);
        payload.addProperty("type", "town");
        syncManager.publishTerritoryDataChange(
            org.leralix.tan.redis.RedisSyncManager.SyncType.TERRITORY_CREATED, payload.toString());
      }
    } catch (Exception ex) {
      TownsAndNations.getPlugin()
          .getLogger()
          .warning("Failed to publish town creation event: " + ex.getMessage());
    }

    return CompletableFuture.completedFuture(newTown);
  }

  private @NotNull String getNextTownID() {
    String townId = "T" + newTownId;
    newTownId++;
    getDatabase().updateNextTownId(newTownId);
    return townId;
  }

  public CompletableFuture<TownData> newTown(String townName) {
    String townId = getNextTownID();

    TownData newTown = new TownData(townId, townName, null);

    putWithInvalidation(townId, newTown).join(); // ✅ SYNC-FIX: Use putWithInvalidation
    return CompletableFuture.completedFuture(newTown);
  }

  public void deleteTown(TownData townData) {
    deleteAsync(townData.getID());

    try {
      org.leralix.tan.redis.RedisSyncManager syncManager =
          TownsAndNations.getPlugin().getRedisSyncManager();
      if (syncManager != null) {
        com.google.gson.JsonObject payload = new com.google.gson.JsonObject();
        payload.addProperty("territoryId", townData.getID());
        syncManager.publishTerritoryDataChange(
            org.leralix.tan.redis.RedisSyncManager.SyncType.TERRITORY_DELETED, payload.toString());
      }
    } catch (Exception ex) {
      TownsAndNations.getPlugin()
          .getLogger()
          .warning("Failed to publish town deletion event: " + ex.getMessage());
    }

    try {
      org.leralix.tan.redis.RedisSyncManager syncManager =
          TownsAndNations.getPlugin().getRedisSyncManager();
      if (syncManager != null) {
        com.google.gson.JsonObject payload = new com.google.gson.JsonObject();
        payload.addProperty("territoryId", townData.getID());
        syncManager.publishTerritoryDataChange(
            org.leralix.tan.redis.RedisSyncManager.SyncType.TERRITORY_DELETED, payload.toString());
      }
    } catch (Exception ex) {
      TownsAndNations.getPlugin()
          .getLogger()
          .warning("Failed to publish town deletion event: " + ex.getMessage());
    }
  }

  public CompletableFuture<TownData> get(ITanPlayer tanPlayer) {
    return get(tanPlayer.getTownId());
  }

  public CompletableFuture<TownData> get(Player player) {
    return PlayerDataStorage.getInstance()
        .get(player)
        .thenCompose(
            tanPlayer -> {
              if (tanPlayer == null) {
                return CompletableFuture.completedFuture(null);
              }
              return get(tanPlayer.getTownId());
            });
  }

  public int getNumberOfTown() {
    return count();
  }

  public boolean isNameUsed(String townName) {
    if (townName == null) {
      return false;
    }

    String selectSQL = "SELECT 1 FROM " + TABLE_NAME + " WHERE town_name = ? LIMIT 1";

    try (Connection conn = getDatabase().getDataSource().getConnection();
        PreparedStatement ps = conn.prepareStatement(selectSQL)) {

      ps.setString(1, townName);

      try (ResultSet rs = ps.executeQuery()) {
        return rs.next();
      }
    } catch (SQLException e) {
      TownsAndNations.getPlugin()
          .getLogger()
          .warning(
              "SQL query failed for town name check, falling back to full scan: " + e.getMessage());

      Map<String, TownData> allTowns = getAllSync();
      for (TownData town : allTowns.values()) {
        if (townName.equals(town.getName())) return true;
      }
    }

    return false;
  }

  public TownData getSync(String id) {
    if (cacheEnabled && cache != null) {
      TownData cached = cache.get(id);
      if (cached != null) {
        return cached;
      }
    }

    get(id)
        .thenAccept(
            town -> {
              if (town != null && cacheEnabled && cache != null) {
                cache.put(id, town);
              }
            });

    return null;
  }

  public TownData getSync(ITanPlayer tanPlayer) {
    return getSync(tanPlayer.getTownId());
  }

  public TownData getSync(Player player) {
    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player).join();
    return getSync(tanPlayer.getTownId());
  }
}
