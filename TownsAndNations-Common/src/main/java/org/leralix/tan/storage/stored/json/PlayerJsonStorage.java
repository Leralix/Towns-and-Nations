package org.leralix.tan.storage.stored.json;

import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.player.NoPlayerData;
import org.leralix.tan.data.player.PlayerData;
import org.leralix.tan.storage.stored.PlayerDataStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class PlayerJsonStorage extends JsonStorage<ITanPlayer> implements PlayerDataStorage {

    private static final ITanPlayer NO_PLAYER = new NoPlayerData();
    

    public PlayerJsonStorage() {
        super("TAN - Players.json",
                new TypeToken<HashMap<String, PlayerData>>() {}.getType(),
                new GsonBuilder()
                        .setPrettyPrinting()
                        .create());
    }

    /**
     * Registers a player in the storage. If the player is already registered, it returns the existing data.
     * @param p The player to register.
     * @return The ITanPlayer instance associated with the registered player.
     */
    public ITanPlayer register(Player p) {
        ITanPlayer tanPlayer = new PlayerData(p);
        put(tanPlayer.getID().toString(), tanPlayer);
        return tanPlayer;
    }

    @Override
    public Collection<ITanPlayer> getAllPlayers() {
        return getAll().values();
    }

    @Override
    public ITanPlayer get(String id){

        if (id == null) {
            return NO_PLAYER;
        }

        ITanPlayer res = dataMap.get(id);
        if (res != null) {
            return res;
        }

        Player newPlayer = Bukkit.getPlayer(UUID.fromString(id));
        if (newPlayer != null) {
            return register(newPlayer);
        }
        throw new RuntimeException("Error : Player ID [" + id + "] has not been found" );
    }

}