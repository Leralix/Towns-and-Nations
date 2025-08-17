package org.leralix.tan.storage.cache;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OfflinePlayerCache {

    private static final Map<UUID, OfflinePlayer> playerMap = new HashMap<>();


    public static OfflinePlayer getOfflinePlayer(UUID uuid){

        if(playerMap.containsKey(uuid)){
            return playerMap.get(uuid);
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        playerMap.put(uuid, offlinePlayer);
        return offlinePlayer;
    }
}
