package org.tan.towns_and_nations.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.tan.towns_and_nations.commands.PlayerData.PlayerDataClass;
import org.tan.towns_and_nations.commands.SubCommand;
import org.tan.towns_and_nations.utils.PlayerStatStorage;


public class AddBalanceCommand extends SubCommand  {
    @Override
    public String getName() {
        return "addmoney";
    }

    @Override
    public String getDescription() {
        return "Add money to a player's balance";
    }

    @Override
    public String getSyntax() {
        return "/tan addmoney playerName amount";
    }

    @Override
    public void perform(Player player, String[] args){
        if (args.length < 3){
            player.sendMessage("Not enough arguments");
            player.sendMessage("Correct Syntax: " + getSyntax());
        }
        else if(args.length == 3){
            PlayerDataClass target = PlayerStatStorage.findStatUUID(Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString());
            int amount = 0;
            try{
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                player.sendMessage( "Invalid Syntax for the amount of money");
                throw new RuntimeException(e);
            }

            target.addToBalance(amount);
            player.sendMessage( "Added "  + amount + " Ecu to " + target.getPlayerName());
        }
        else {
            player.sendMessage("Too many arguments");
            player.sendMessage("Correct Syntax: " + getSyntax());
        }
    }

}



