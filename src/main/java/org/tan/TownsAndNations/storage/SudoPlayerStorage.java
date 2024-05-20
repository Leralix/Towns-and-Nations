package org.tan.TownsAndNations.storage;

import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.PlayerData;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to store the UUID of players who are using the sudo command (admin mode).
 */
public class SudoPlayerStorage {
    /**
     * List of UUID of players who are registered in the sudo mode.
     */
    private static final List<String> sudoPlayersID = new ArrayList<>();

    /**
     * Add a player to the sudo list.
     * @param player    Player to add to the sudo list.
     */
    public static void addSudoPlayer(Player player){
        addSudoPlayer(player.getUniqueId().toString());
    }

    /**
     *  Add a player to the sudo list.
     * @param playerData    Player to add to the sudo list.
     */
    public static void addSudoPlayer(PlayerData playerData){
        addSudoPlayer(playerData.getID());
    }

    /**
     * Add a player to the sudo list.
     * @param playerID  ID of player to add to the sudo list.
     */
   public static void addSudoPlayer(String playerID){
       sudoPlayersID.add(playerID);
   }

    /**
     * Remove a player from the sudo list.
     * @param player    Player to remove from the sudo list.
     */
    public static void removeSudoPlayer(PlayerData player){
        removeSudoPlayer(player.getID());
    }

    /**
     * Remove a player from the sudo list.
     * @param player    Player to remove from the sudo list.
     */
    public static void removeSudoPlayer(Player player){
        removeSudoPlayer(player.getUniqueId().toString());
    }

    /**
     * Remove a player from the sudo list.
     * @param playerID  Player to remove from the sudo list.
     */
    public static void removeSudoPlayer(String playerID){
         sudoPlayersID.remove(playerID);
    }

    /**
     * Check if a player is in the sudo list.
     * @param player    Player to check.
     * @return          True if the player is in the sudo list, false otherwise.
     */
    public static boolean isSudoPlayer(PlayerData player){
        return isSudoPlayer(player.getID());
    }

    /**
     * Check if a player is in the sudo list.
     * @param player    Player to check.
     * @return          True if the player is in the sudo list, false otherwise.
     */
    public static boolean isSudoPlayer(Player player){
        return isSudoPlayer(player.getUniqueId().toString());
    }

    /**
     * Check if a player is in the sudo list.
     * @param playerID  ID of player to check.
     * @return          True if the player is in the sudo list, false otherwise.
     */
    public static boolean isSudoPlayer(String playerID){
        return sudoPlayersID.contains(playerID);
    }


}
