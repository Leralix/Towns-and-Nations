package org.leralix.tan.storage.stored;

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
    public void put(String id, ITanPlayer obj) {

        String jsonData = gson.toJson(obj, typeToken);
        String upsertSQL = "INSERT INTO " + tableName + " (id, player_name, town_name, nation_name, data) VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE player_name = VALUES(player_name), town_name = VALUES(town_name), nation_name = VALUES(nation_name), data = VALUES(data)";

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
    }

    public ITanPlayer register(Player p) {
        ITanPlayer tanPlayer = new PlayerData(p);
        return register(tanPlayer);
    }
    ITanPlayer register(ITanPlayer p) {
        put(p.getID(), p);
        return p;
    }

    public ITanPlayer get(OfflinePlayer player) {
        return get(player.getUniqueId().toString());
    }

    public ITanPlayer get(Player player) {
        return get(player.getUniqueId().toString());
    }

    public ITanPlayer get(UUID playerID) {
        return get(playerID.toString());
    }

    @Override
    public ITanPlayer get(String id){

        if(id == null)
            return NO_PLAYER;

        // Try to get from database first
        ITanPlayer res = super.get(id);
        if(res != null)
            return res;

        // If not in database, try to create from online player
        Player newPlayer = Bukkit.getPlayer(UUID.fromString(id));
        if(newPlayer != null){
            return register(newPlayer);
        }
        throw new RuntimeException("Error : Player ID [" + id + "] has not been found" );
    }

    @Override
    public void reset() {
        instance = null;
    }


}