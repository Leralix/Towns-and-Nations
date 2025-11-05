package org.leralix.tan.storage.stored;

import org.leralix.tan.utils.FoliaScheduler;

import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.NoPlayerData;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.TownsAndNations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerDataStorage extends DatabaseStorage<ITanPlayer> {

    private static final String ERROR_MESSAGE = "Error while creating player storage";
    private static final String TABLE_NAME = "tan_players";

    private static PlayerDataStorage instance;

    private static ITanPlayer NO_PLAYER;

    private PlayerDataStorage() {
        super(TABLE_NAME,
                ITanPlayer.class,
                new GsonBuilder()
                        .registerTypeAdapter(ITanPlayer.class, new ITanPlayerAdapter())
                        .setPrettyPrinting()
                        .create());
    }

    public static synchronized PlayerDataStorage getInstance() {
        if (instance == null) {
            instance = new PlayerDataStorage();
            NO_PLAYER = new NoPlayerData();
        }
        return instance;
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

            // Migration: Add player_name column if it doesn't exist
            try (ResultSet rs = conn.getMetaData().getColumns(null, null, TABLE_NAME, "player_name")) {
                if (!rs.next()) {
                    stmt.executeUpdate("ALTER TABLE %s ADD COLUMN player_name VARCHAR(255)".formatted(TABLE_NAME));
                    TownsAndNations.getPlugin().getLogger().info("Added player_name column to " + TABLE_NAME);
                }
            }

            // Migration: Add town_name column if it doesn't exist
            try (ResultSet rs = conn.getMetaData().getColumns(null, null, TABLE_NAME, "town_name")) {
                if (!rs.next()) {
                    stmt.executeUpdate("ALTER TABLE %s ADD COLUMN town_name VARCHAR(255)".formatted(TABLE_NAME));
                    TownsAndNations.getPlugin().getLogger().info("Added town_name column to " + TABLE_NAME);
                }
            }

            // Migration: Add nation_name column if it doesn't exist
            try (ResultSet rs = conn.getMetaData().getColumns(null, null, TABLE_NAME, "nation_name")) {
                if (!rs.next()) {
                    stmt.executeUpdate("ALTER TABLE %s ADD COLUMN nation_name VARCHAR(255)".formatted(TABLE_NAME));
                    TownsAndNations.getPlugin().getLogger().info("Added nation_name column to " + TABLE_NAME);
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
        String createPlayerNameIndexSQL = "CREATE INDEX IF NOT EXISTS idx_player_name ON " + TABLE_NAME + " (player_name)";
        String createTownNameIndexSQL = "CREATE INDEX IF NOT EXISTS idx_player_town ON " + TABLE_NAME + " (town_name)";
        String createNationNameIndexSQL = "CREATE INDEX IF NOT EXISTS idx_player_nation ON " + TABLE_NAME + " (nation_name)";

        try (Connection conn = getDatabase().getDataSource().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createPlayerNameIndexSQL);
            stmt.execute(createTownNameIndexSQL);
            stmt.execute(createNationNameIndexSQL);
            TownsAndNations.getPlugin().getLogger().info("Created indexes on " + TABLE_NAME);
        } catch (SQLException e) {
            TownsAndNations.getPlugin().getLogger().warning(
                "Error creating indexes for " + TABLE_NAME + ": " + e.getMessage()
            );
        }
    }

    @Override
    public void put(String id, ITanPlayer obj) {
        if (id == null || obj == null) {
            return;
        }

        String jsonData = gson.toJson(obj, typeToken);

        // Use database-specific UPSERT syntax
        String upsertSQL;
        if (getDatabase().isMySQL()) {
            upsertSQL = "INSERT INTO " + tableName + " (id, player_name, town_name, nation_name, data) VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE player_name = VALUES(player_name), town_name = VALUES(town_name), nation_name = VALUES(nation_name), data = VALUES(data)";
        } else {
            // SQLite syntax
            upsertSQL = "INSERT OR REPLACE INTO " + tableName + " (id, player_name, town_name, nation_name, data) VALUES (?, ?, ?, ?, ?)";
        }

        FoliaScheduler.runTaskAsynchronously(TownsAndNations.getPlugin(), () -> {
            try (Connection conn = getDatabase().getDataSource().getConnection();
                 PreparedStatement ps = conn.prepareStatement(upsertSQL)) {

                ps.setString(1, id);
                ps.setString(2, obj.getNameStored()); // Set player_name
                ps.setString(3, obj.getTownName()); // Set town_name
                ps.setString(4, obj.getNationName()); // Set nation_name
                ps.setString(5, jsonData);
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

    public CompletableFuture<ITanPlayer> register(Player p) {
        ITanPlayer tanPlayer = new PlayerData(p);
        return register(tanPlayer);
    }
    CompletableFuture<ITanPlayer> register(ITanPlayer p) {
        CompletableFuture<ITanPlayer> future = new CompletableFuture<>();
        put(p.getID(), p);
        future.complete(p); // Assuming put() will eventually complete the storage. This is a simplification.
        return future;
    }

    public CompletableFuture<ITanPlayer> get(OfflinePlayer player) {
        return get(player.getUniqueId().toString());
    }

    public CompletableFuture<ITanPlayer> get(Player player) {
        return get(player.getUniqueId().toString());
    }

    public CompletableFuture<ITanPlayer> get(UUID playerID) {
        return get(playerID.toString());
    }

    @Override
    public CompletableFuture<ITanPlayer> get(String id){
        CompletableFuture<ITanPlayer> future = new CompletableFuture<>();
        if(id == null) {
            future.complete(NO_PLAYER);
            return future;
        }

        super.get(id).thenAccept(res -> {
            if(res != null) {
                future.complete(res);
            } else {
                // If not in database, try to create from online player
                // MUST execute Bukkit.getPlayer() on the main thread (Folia/Paper requirement)
                FoliaScheduler.runTask(TownsAndNations.getPlugin(), () -> {
                    Player newPlayer = Bukkit.getPlayer(UUID.fromString(id));
                    if(newPlayer != null){
                        // Create PlayerData and register it
                        ITanPlayer newTanPlayer = new PlayerData(newPlayer);
                        // Register asynchronously
                        register(newTanPlayer).thenAccept(registeredPlayer -> {
                            future.complete(registeredPlayer);
                        }).exceptionally(ex -> {
                            future.completeExceptionally(ex);
                            return null;
                        });
                    } else {
                        // If player not found in DB and not online, complete with NO_PLAYER or throw exception
                        future.completeExceptionally(new RuntimeException("Error : Player ID [" + id + "] has not been found" ));
                    }
                });
            }
        }).exceptionally(ex -> {
            future.completeExceptionally(ex);
            return null;
        });

        return future;
    }

    @Override
    public void reset() {
        instance = null;
    }

    /**
     * Synchronous get method for backward compatibility
     *
     * IMPORTANT: To prevent Folia deadlocks, this method now uses cache-only access.
     * This is safe because player data is pre-loaded into cache during normal operations.
     * If you need guaranteed fresh data from the database, use get() with async handling.
     *
     * @param id The ID of the player
     * @return The player data from cache, or NO_PLAYER if not in cache
     */
    public ITanPlayer getSync(String id) {
        ITanPlayer cached = getFromCacheOnly(id);
        return cached != null ? cached : NO_PLAYER;
    }

    public ITanPlayer getSync(UUID playerID) {
        return getSync(playerID.toString());
    }

    public ITanPlayer getSync(Player player) {
        return getSync(player.getUniqueId());
    }

    public ITanPlayer getSync(OfflinePlayer player) {
        return getSync(player.getUniqueId());
    }


}