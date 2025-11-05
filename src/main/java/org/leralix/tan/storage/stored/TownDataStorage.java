package org.leralix.tan.storage.stored;

import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import org.bukkit.OfflinePlayer;
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class TownDataStorage extends DatabaseStorage<TownData>{

    private static final String TABLE_NAME = "tan_towns";
    private static TownDataStorage instance;

    private int newTownId;

    private TownDataStorage() {
        super(TABLE_NAME,
                TownData.class,
                new GsonBuilder()
                        .registerTypeAdapter(new TypeToken<Map<ChunkPermissionType, RelationPermission>>() {}.getType(), new EnumMapKeyValueDeserializer<>(ChunkPermissionType.class, RelationPermission.class))
                        .registerTypeAdapter(new TypeToken<Map<TownRelation, List<String>>>() {}.getType(),new EnumMapDeserializer<>(TownRelation.class, new TypeToken<List<String>>(){}.getType()))
                        .registerTypeAdapter(new TypeToken<List<RelationPermission>>() {}.getType(),new EnumMapDeserializer<>(RelationPermission.class, new TypeToken<List<String>>(){}.getType()))
                        .registerTypeAdapter(AbstractOwner.class, new OwnerDeserializer())
                        .registerTypeAdapter(ICustomIcon.class, new IconAdapter())
                        .setPrettyPrinting()
                        .create());
        loadNextTownId();
    }

    @Override
    protected void createTable() {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS %s (
                id VARCHAR(255) PRIMARY KEY,
                data TEXT NOT NULL
            )
        """.formatted(TABLE_NAME);

        try (Connection conn = getDatabase().getDataSource().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);

            // Migration: Add town_name column if it doesn't exist
            try (ResultSet rs = conn.getMetaData().getColumns(null, null, TABLE_NAME, "town_name")) {
                if (!rs.next()) {
                    stmt.executeUpdate("ALTER TABLE %s ADD COLUMN town_name VARCHAR(255) NULL".formatted(TABLE_NAME));
                    TownsAndNations.getPlugin().getLogger().info("Added town_name column to " + TABLE_NAME);
                }
            }

        } catch (SQLException e) {
            TownsAndNations.getPlugin().getLogger().severe(
                "Error creating table " + TABLE_NAME + ": " + e.getMessage()
            );
        }
    }

    @Override
    protected void createIndexes() {
        // PERFORMANCE FIX: Add indexes for frequently queried columns
        String createNameIndexSQL = "CREATE INDEX IF NOT EXISTS idx_town_name ON " + TABLE_NAME + " (town_name)";

        try (Connection conn = getDatabase().getDataSource().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createNameIndexSQL);
            TownsAndNations.getPlugin().getLogger().info("Created index idx_town_name on " + TABLE_NAME);
        } catch (SQLException e) {
            TownsAndNations.getPlugin().getLogger().warning(
                "Error creating indexes for " + TABLE_NAME + ": " + e.getMessage()
            );
        }
    }

    @Override
    public void put(String id, TownData obj) {
        if (id == null || obj == null) {
            return;
        }

        String jsonData = gson.toJson(obj, typeToken);
        String upsertSQL = "INSERT INTO " + tableName + " (id, town_name, data) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE town_name = VALUES(town_name), data = VALUES(data)";

        FoliaScheduler.runTaskAsynchronously(TownsAndNations.getPlugin(), () -> {
            try (Connection conn = getDatabase().getDataSource().getConnection();
                 PreparedStatement ps = conn.prepareStatement(upsertSQL)) {

                ps.setString(1, id);
                ps.setString(2, obj.getName()); // Set town_name
                ps.setString(3, jsonData);
                ps.executeUpdate();

                // Update cache
                if (cacheEnabled && cache != null) {
                    synchronized (cache) {
                        cache.put(id, obj);
                    }
                }

            } catch (SQLException e) {
                TownsAndNations.getPlugin().getLogger().severe(
                    "Error storing " + typeClass.getSimpleName() + " with ID " + id + ": " + e.getMessage()
                );
            }
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
        if (instance == null)
            instance = new TownDataStorage();
        return instance;
    }

    public CompletableFuture<TownData> newTown(String townName, ITanPlayer tanPlayer){
        String townId = getNextTownID();
        TownData newTown = new TownData(townId, townName, tanPlayer);

        put(townId,newTown);
        return CompletableFuture.completedFuture(newTown);
    }

    private @NotNull String getNextTownID() {
        String townId = "T"+ newTownId;
        newTownId++;
        getDatabase().updateNextTownId(newTownId);
        return townId;
    }

    public CompletableFuture<TownData> newTown(String townName){
        String townId = getNextTownID();

        TownData newTown = new TownData(townId, townName, null);

        put(townId,newTown);
        return CompletableFuture.completedFuture(newTown);
    }


    public void deleteTown(TownData townData) {
        delete(townData.getID());
    }


    public CompletableFuture<TownData> get(ITanPlayer tanPlayer){
        return get(tanPlayer.getTownId());
    }

    public CompletableFuture<TownData> get(Player player){
        return PlayerDataStorage.getInstance().get(player).thenCompose(tanPlayer -> {
            if (tanPlayer == null) {
                return CompletableFuture.completedFuture(null);
            }
            return get(tanPlayer.getTownId());
        });
    }


    public int getNumberOfTown() {
        return count();
    }

    public boolean isNameUsed(String townName){
        if (townName == null) {
            return false;
        }

        // Optimized: scan JSON data for name instead of deserializing all objects
        String selectSQL = "SELECT 1 FROM " + TABLE_NAME + " WHERE json_extract(data, '$.name') = ? LIMIT 1";

        try (Connection conn = getDatabase().getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(selectSQL)) {

            ps.setString(1, townName);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            // Fallback to the old method if json_extract is not supported
            TownsAndNations.getPlugin().getLogger().warning(
                "json_extract not supported, falling back to full scan: " + e.getMessage()
            );

            for (TownData town : getAll().values()){
                if(townName.equals(town.getName()))
                    return true;
            }
        }

        return false;
    }

    /**
     * Synchronous get method for backward compatibility
     * WARNING: This blocks the current thread. Use get() with thenAccept() for async operations.
     * @param id The ID of the town
     * @return The town data, or null if not found
     */
    public TownData getSync(String id) {
        try {
            return get(id).join();
        } catch (Exception e) {
            TownsAndNations.getPlugin().getLogger().warning(
                "Error getting town data synchronously: " + e.getMessage()
            );
            return null;
        }
    }

    public TownData getSync(ITanPlayer tanPlayer) {
        return getSync(tanPlayer.getTownId());
    }

    public TownData getSync(Player player) {
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getSync(player);
        return getSync(tanPlayer.getTownId());
    }

}
