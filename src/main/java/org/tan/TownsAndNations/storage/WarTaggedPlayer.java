package org.tan.TownsAndNations.storage;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.DataClass.territoryData.TownData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;
import org.tan.TownsAndNations.utils.ChatUtils;

import java.util.*;

/**
 * This class is used to store the players that are in war with a town.
 */
public class WarTaggedPlayer {
    /**
     * The key is the town ID and the value is a set of player UUIDs.
     * Every player in the set is in war with the town and is allowed to attack it.
     */
    private static final Map<String, List<String>> warTagged = new HashMap<>();

    /**
     * Add a player to the list of players that are in war with a town.
     * @param attackedTownID    The town ID of the town that is being attacked.
     * @param players           The set of player UUIDs that are in war with the town.
     */
    public static void addPlayersToTown(String attackedTownID, Collection<String> players){
        if(!warTagged.containsKey(attackedTownID)){
            warTagged.put(attackedTownID,new ArrayList<>());
        }

        for (String playerUUID : players) {
            warTagged.get(attackedTownID).add(playerUUID);
            new BukkitRunnable() {
                @Override
                public void run() {
                    warTagged.get(attackedTownID).remove(playerUUID);

                    Player player = Bukkit.getPlayer(playerUUID);
                    if(player != null)
                        player.sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_ATTACK_FINISHED.get(TownDataStorage.get(attackedTownID).getName()));;
                }
            }.runTaskLater(TownsAndNations.getPlugin(), 20 * 60 * 60);
        }
    }

    /**
     * Check if a player is in war with a town.
     * @param playerID      The player UUID.
     * @param townID        The town ID.
     * @return              True if the player is in war with the town, false otherwise.
     */
    public static boolean isPlayerInWarWithTown(String playerID, String townID){
        if(!warTagged.containsKey(townID))
            return false;
        return warTagged.get(townID).contains(playerID);
    }
    /**
     * Check if a player is in war with a town.
     * @param player        The player.
     * @param townID        The town ID.
     * @return              True if the player is in war with the town, false otherwise.
     */
    public static boolean isPlayerInWarWithTown(Player player, String townID){
        return isPlayerInWarWithTown(player.getUniqueId().toString(),townID);
    }
    /**
     * Check if a player is in war with a town.
     * @param playerID      The player UUID.
     * @param town          The town data.
     * @return              True if the player is in war with the town, false otherwise.
     */
    public static boolean isPlayerInWarWithTown(String playerID, TownData town){
        return isPlayerInWarWithTown(playerID,town.getID());

    }
    /**
     * Check if a player is in war with a town.
     * @param player        The player.
     * @param town          The town data.
     * @return              True if the player is in war with the town, false otherwise.
     */
    public static boolean isPlayerInWarWithTown(Player player, TownData town){
        return isPlayerInWarWithTown(player.getUniqueId().toString(),town.getID());
    }

    /**
     * Remove a player from the list of players that are in war with a town.
     * @param playerData    The player to remove.
     */
    public static void removePlayer(PlayerData playerData){
        removePlayer(playerData.getID());
    }
    /**
     * Remove a player from the list of players that are in war with a town.
     * @param playerID      The ID of the player to remove.
     */
    public static void removePlayer(String playerID){
        for (List<String> playerList : warTagged.values()) {
            playerList.remove(playerID);
        }
    }



}
