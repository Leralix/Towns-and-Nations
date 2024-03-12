package org.tan.TownsAndNations.storage;

import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.PlayerData;

import java.util.ArrayList;
import java.util.List;

public class SudoPlayerStorage {

    private static final List<String> sudoPlayersID = new ArrayList<>();


    public static void addSudoPlayer(Player player){
        addSudoPlayer(player.getUniqueId().toString());
    }
    public static void addSudoPlayer(PlayerData playerData){
        addSudoPlayer(playerData.getID());
    }
   public static void addSudoPlayer(String playerID){
       sudoPlayersID.add(playerID);
   }
    public static void removeSudoPlayer(PlayerData player){
        removeSudoPlayer(player.getID());
    }
    public static void removeSudoPlayer(Player player){
        removeSudoPlayer(player.getUniqueId().toString());
    }
    public static void removeSudoPlayer(String playerID){
         sudoPlayersID.remove(playerID);
    }
    public static boolean isSudoPlayer(PlayerData player){
        return isSudoPlayer(player.getID());
    }
    public static boolean isSudoPlayer(Player player){
        return isSudoPlayer(player.getUniqueId().toString());
    }
    public static boolean isSudoPlayer(String playerID){
        return sudoPlayersID.contains(playerID);
    }


}
