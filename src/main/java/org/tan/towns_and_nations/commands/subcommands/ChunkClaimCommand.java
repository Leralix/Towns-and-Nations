package org.tan.towns_and_nations.commands.subcommands;

import org.tan.towns_and_nations.commands.SubCommand;


import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;


public class ChunkClaimCommand extends SubCommand  {
    @Override
    public String getName() {
        return "claim";
    }


    @Override
    public String getDescription() {
        return "claim the chunk you in witch you are";
    }
    public int getArguments(){ return 2;}


    @Override
    public String getSyntax() {
        return "/tan claim";
    }

    @Override
    public void perform(Player player, String[] args){
        if (args.length == 1){

            player.getName();
            player.getLocation().getChunk();
            player.sendMessage("You successfully claimed this chunk!");
        }else if(args.length > 1){
            player.sendMessage("Too many arguments");
            player.sendMessage("Correct Syntax: /tan claim");
        }

    }

}



