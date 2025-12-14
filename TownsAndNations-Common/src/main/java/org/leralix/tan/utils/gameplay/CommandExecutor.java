package org.leralix.tan.utils.gameplay;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.war.PlannedAttack;

public class CommandExecutor {

    private CommandExecutor(){
        throw new AssertionError("Static class");
    }

    public static void applyStartAttackCommands(PlannedAttack attackData){

        for(String command : Constants.getOnceStartCommands()){
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }

        for(String command : Constants.getPerPlayerStartCommands()){
            for(OfflinePlayer player : attackData.getAllOfflinePlayers()){
                String playerName = player.getName();
                if(playerName == null){
                    continue;
                }
                String completedCommand = command.replace("%PLAYER%", playerName);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), completedCommand);
            }
        }

    }

    public static void applyEndWarCommands(PlannedAttack attackData){
        for(String command : Constants.getOnceEndCommands()){
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }

        for(String command : Constants.getPerPlayerEndCommands()){
            for(OfflinePlayer player : attackData.getAllOfflinePlayers()){
                String playerName = player.getName();
                if(playerName == null){
                    continue;
                }
                String completedCommand = command.replace("%PLAYER%", playerName);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), completedCommand);
            }
        }
    }

}
