package org.tan.towns_and_nations.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.tan.towns_and_nations.PlayerData.PlayerDataClass;
import org.tan.towns_and_nations.commands.SubCommand;
import org.tan.towns_and_nations.utils.PlayerStatStorage;

import java.io.IOException;
import java.util.ArrayList;

public class DebugCommand extends SubCommand {
    @Override
    public String getName() {
        return "debug";
    }

    @Override
    public String getDescription() {
        return "debug everything in Towns and Nations";
    }

    @Override
    public String getSyntax() {
        return "/tan debug <DebugCommand>";
    }

    @Override
    public void perform(Player player, String[] args){
        if (args.length < 2){
            player.sendMessage("Not enough arguments");
            player.sendMessage("Correct Syntax: " + getSyntax());
        }
        else if(args.length == 2){
            switch(args[1]){

                case "playerstats":
                    ArrayList<PlayerDataClass> stats = PlayerStatStorage.getStats();
                    for (PlayerDataClass stat : stats) {
                        player.sendMessage(stat.getPlayerName() + ": " + stat.getBalance());
                    }
                    break;

                case "savestats":
                    try {
                        PlayerStatStorage.saveStats();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;

                default:
                    player.sendMessage("Unkown debug");
            }

        }
        else {
            player.sendMessage("Too many arguments");
            player.sendMessage("Correct Syntax: " + getSyntax());
        }
    }

}
