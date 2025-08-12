package org.leralix.tan.storage.stored;

import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.NoPlayerData;
import org.leralix.tan.dataclass.PlayerData;

import java.util.HashMap;
import java.util.UUID;

public class PlayerDataStorage extends JsonStorage<ITanPlayer> {

    private static final String ERROR_MESSAGE = "Error while creating player storage";


    private static PlayerDataStorage instance;

    private static ITanPlayer NO_PLAYER;

    private PlayerDataStorage() {
        super("TAN - Players.json",
                new TypeToken<HashMap<String, PlayerData>>() {}.getType(),
                new GsonBuilder()
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

        ITanPlayer res = dataMap.get(id);
        if(res != null)
            return res;

        Player newPlayer = Bukkit.getPlayer(UUID.fromString(id));
        if(newPlayer != null){
            return register(newPlayer);
        }
        throw new RuntimeException("Error : Player ID [" + id + "] has not been found" );
    }


}