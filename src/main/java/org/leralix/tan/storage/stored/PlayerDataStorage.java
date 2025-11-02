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
        } catch (SQLException e) {
            TownsAndNations.getPlugin().getLogger().severe(
                "Error creating table " + TABLE_NAME + ": " + e.getMessage()
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