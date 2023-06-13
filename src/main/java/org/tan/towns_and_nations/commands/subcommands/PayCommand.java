package org.tan.towns_and_nations.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.tan.towns_and_nations.DataClass.PlayerDataClass;
import org.tan.towns_and_nations.commands.SubCommand;
import org.tan.towns_and_nations.storage.PlayerStatStorage;


public class PayCommand extends SubCommand  {
    @Override
    public String getName() {
        return "pay";
    }

    @Override
    public String getDescription() {
        return "give money to another player";
    }

    @Override
    public String getSyntax() {
        return "/tan pay <playerName> <amount>";
    }

    public int getArguments(){
        return 3;
    }


    @Override
    public void perform(Player player, String[] args){
        if (args.length < 3){
            player.sendMessage(ChatColor.GOLD + "[TAN]" + ChatColor.WHITE +  " Not enough arguments");
            player.sendMessage(ChatColor.GOLD + "[TAN]" + ChatColor.WHITE +  " Correct Syntax: " + getSyntax());
        }
        else if(args.length == 3){
            PlayerDataClass receiver = PlayerStatStorage.getStatUUID(Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString());
            PlayerDataClass sender = PlayerStatStorage.getStatUUID(player.getUniqueId().toString());
            int amount = 0;

            try{
                amount = Integer.parseInt(args[2]);

            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.GOLD + "[TAN]" + ChatColor.WHITE +  " Invalid Syntax for the amount of money");
                throw new RuntimeException(e);
            }
            if(amount <1){
                player.sendMessage(ChatColor.GOLD + "[TAN]" + ChatColor.WHITE +  " You need to send a least 1$");
                return;
            }
            if(sender.getBalance() < amount){
                player.sendMessage(ChatColor.GOLD + "[TAN]" + ChatColor.WHITE +  " You do not have enough money. You need " + ChatColor.YELLOW + (amount - sender.getBalance()) + ChatColor.WHITE + " more");
                return;
            }

            sender.removeFromBalance(amount);
            receiver.addToBalance(amount);
            player.sendMessage(ChatColor.GOLD + "[TAN]" + ChatColor.WHITE +  " Sending "  + ChatColor.YELLOW + amount + ChatColor.WHITE + " $ to " + receiver.getPlayerName());
            Bukkit.getOfflinePlayer(args[1]).getPlayer().sendMessage(ChatColor.GOLD + "[TAN]" + ChatColor.WHITE +" Received "  + ChatColor.YELLOW + amount + ChatColor.WHITE + " Ecu to " + receiver.getPlayerName());

        }
        else {
            player.sendMessage(ChatColor.GOLD + "[TAN]" + ChatColor.WHITE +  " Too many arguments");
            player.sendMessage(ChatColor.GOLD + "[TAN]" + ChatColor.WHITE +  " Correct Syntax: " + getSyntax());
        }
    }



}



