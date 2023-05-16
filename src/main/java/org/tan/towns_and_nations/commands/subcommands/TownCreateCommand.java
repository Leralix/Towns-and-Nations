package org.tan.towns_and_nations.commands.subcommands;

import org.tan.towns_and_nations.commands.SubCommand;


import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;


public class TownCreateCommand extends SubCommand {
    @Override
    public String getName() {
        return "create";
    }

    @Override
    public String getDescription() {
        return "Create a town";
    }
    public int getArguments(){
        return 2;
    }
    @Override
    public String getSyntax() {
        return "/tan create <town name>";
    }

    @Override
    public void perform(Player player, String[] args){
        if (args.length == 2){

            String townName = args[1];

            player.sendMessage("You succesfully created the town " + townName);
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);

        }else if(args.length == 1){
            player.sendMessage("You did not provide a name");
            player.sendMessage("Correct Syntax: /tan create <town name>");
        }else if(args.length > 2){
            player.sendMessage("You cannot use space in the town's name");
            player.sendMessage("Correct Syntax: /tan create <town name>");
        }

    }
}



