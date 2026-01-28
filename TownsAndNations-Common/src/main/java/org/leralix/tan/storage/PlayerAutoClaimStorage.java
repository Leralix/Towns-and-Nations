package org.leralix.tan.storage;

import org.bukkit.entity.Player;
import org.leralix.tan.gui.scope.ClaimType;

import java.util.HashMap;
import java.util.Map;

public class PlayerAutoClaimStorage {

    private static final Map<Player, ClaimType> playerList = new HashMap<>();

    public static void addPlayer(Player player, ClaimType chunkType) {
        playerList.put(player, chunkType);
    }

    public static void removePlayer(Player player) {
        playerList.remove(player);
    }

    public static boolean containsPlayer(Player player) {
        return playerList.containsKey(player);
    }

    public static ClaimType getChunkType(Player player) {
        return playerList.get(player);
    }

}
