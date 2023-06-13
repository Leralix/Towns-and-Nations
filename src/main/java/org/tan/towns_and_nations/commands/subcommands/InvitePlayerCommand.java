package org.tan.towns_and_nations.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.tan.towns_and_nations.DataClass.TownDataClass;
import org.tan.towns_and_nations.commands.SubCommand;
import org.tan.towns_and_nations.utils.ChatUtils;
import org.tan.towns_and_nations.storage.PlayerStatStorage;
import org.tan.towns_and_nations.storage.TownDataStorage;
import org.tan.towns_and_nations.storage.TownInviteDataStorage;

public class InvitePlayerCommand extends SubCommand {
    @Override
    public String getName() {
        return "invite";
    }


    @Override
    public String getDescription() {
        return "invite to the town";
    }
    public int getArguments(){ return 2;}


    @Override
    public String getSyntax() {
        return "/tan invite <playerName>";
    }

    @Override
    public void perform(Player player, String[] args){
        if (args.length <= 1){
            player.sendMessage(ChatColor.GOLD + "[TAN]" + ChatColor.WHITE +  " Not enough arguments");
            player.sendMessage(ChatColor.GOLD + "[TAN]" + ChatColor.WHITE +  " Correct Syntax: " + getSyntax());

        }else if(args.length == 2){
            Player invite = Bukkit.getPlayer(args[1]);
            if(invite == null){
                player.sendMessage(ChatColor.GOLD + "[TAN]" + ChatColor.WHITE +" Invalid name, or Player isn't connected");
            }
            else{
                TownDataClass town = TownDataStorage.getTown(PlayerStatStorage.getStatUUID(player.getUniqueId().toString()).getTownId());
                TownInviteDataStorage.addInvitation(invite.getUniqueId().toString(),town.getTownId() );

                player.sendMessage(ChatColor.GOLD + "[TAN]" + ChatColor.WHITE + " Invitation sent to " + ChatColor.YELLOW + invite.getName());
                invite.sendMessage(ChatColor.GOLD + "[TAN]" + ChatColor.WHITE + " You have been invited by "+ ChatColor.YELLOW + player.getName() + ChatColor.WHITE + " to his town: " + ChatColor.YELLOW + town.getTownName());
                invite.sendMessage(ChatColor.GOLD + "[TAN]" + ChatColor.WHITE + " To join his town, type " + ChatColor.BOLD + ChatColor.YELLOW +  "/tan join "  + town.getTownId());

                ChatUtils.sendClickableCommand(invite,  ChatColor.GOLD + "" +ChatColor.BOLD + "[Or click here]",  "tan join "  + town.getTownId());



            }
        }else {
            player.sendMessage(ChatColor.GOLD + "[TAN]" + ChatColor.WHITE +  " Too many arguments");
            player.sendMessage(ChatColor.GOLD + "[TAN]" + ChatColor.WHITE +  " Correct Syntax: " + getSyntax());
        }

    }





}


