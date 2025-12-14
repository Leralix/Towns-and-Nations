package org.leralix.tan.storage;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.file.FileUtil;
import org.leralix.tan.utils.text.TanChatUtils;

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


    public static void swap(Player player) {
        if(sudoPlayersID.contains(player.getUniqueId().toString())){
            removeSudoPlayer(player);
            TanChatUtils.message(player, Lang.SUDO_PLAYER_REMOVED.get(player, player.getName()), SoundEnum.MINOR_GOOD);
            FileUtil.addLineToHistory(Lang.HISTORY_SUDO_MODE_REMOVED.get(player.getName(),player.getName()));
        }
        else{
            addSudoPlayer(player);
            TanChatUtils.message(player, Lang.SUDO_PLAYER_ADDED.get(player, player.getName()), SoundEnum.MINOR_GOOD);
            FileUtil.addLineToHistory(Lang.HISTORY_SUDO_MODE.get(player.getName(),player.getName()));
        }
    }

    public static void swap(CommandSender commandSender, Player target) {
        if(sudoPlayersID.contains(target.getUniqueId().toString())){
            removeSudoPlayer(target);
            TanChatUtils.message(commandSender, Lang.SUDO_PLAYER_REMOVED.get(target.getName()), SoundEnum.MINOR_GOOD);
            TanChatUtils.message(target, Lang.SUDO_PLAYER_REMOVED.get(target, target.getName()), SoundEnum.MINOR_GOOD);
            FileUtil.addLineToHistory(Lang.HISTORY_SUDO_MODE_REMOVED.get(commandSender.getName(),target.getName()));
        }
        else{
            addSudoPlayer(target);
            TanChatUtils.message(commandSender, Lang.SUDO_PLAYER_ADDED.get(target.getName()), SoundEnum.MINOR_GOOD);
            TanChatUtils.message(target, Lang.SUDO_PLAYER_ADDED.get(target, target.getName()), SoundEnum.MINOR_GOOD);
            FileUtil.addLineToHistory(Lang.HISTORY_SUDO_MODE.get(commandSender.getName(),target.getName()));
        }
    }
}
