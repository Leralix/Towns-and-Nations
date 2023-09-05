package org.tan.TownsAndNations.storage;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.TownsAndNations;

import java.util.*;

public class WarTaggedPlayer {

    private static Map<String, HashSet<String>> warTagged = new HashMap<>();


    public static void addPlayersToTown(String attackedTownID, HashSet<String> players){

        if(!warTagged.containsKey(attackedTownID)){
            warTagged.put(attackedTownID,new HashSet<>());
        }

        for (String playerUUID : players) {
            warTagged.get(attackedTownID).add(playerUUID);

            new BukkitRunnable() {
                @Override
                public void run() {
                    warTagged.get(attackedTownID).remove(playerUUID);
                }
            }.runTaskLater(TownsAndNations.getPlugin(), 20 * 60 * 60); // 20 ticks * 60 secondes * 60 minutes = 1 heure
        }
    }

    public static boolean isPlayerInWarWithTown(String playerID, String townID){
        if(!warTagged.containsKey(townID))
            return false;
        return warTagged.get(townID).contains(playerID);
    }

    public static boolean isPlayerInWarWithTown(Player player, String townID){
        return isPlayerInWarWithTown(player.getUniqueId().toString(),townID);
    }

    public static boolean isPlayerInWarWithTown(String playerID, TownData town){
        return isPlayerInWarWithTown(playerID,town.getID());

    }

    public static boolean isPlayerInWarWithTown(Player player, TownData town){
        return isPlayerInWarWithTown(player.getUniqueId().toString(),town.getID());
    }




}
