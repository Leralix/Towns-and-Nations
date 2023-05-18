package org.tan.towns_and_nations.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.tan.towns_and_nations.PlayerData.PlayerDataClass;
import org.tan.towns_and_nations.commands.SubCommand;
import org.tan.towns_and_nations.utils.PlayerStatStorage;


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
        return "/tan pay playerName amount";
    }

    public int getArguments(){
        return 3;
    }


    @Override
    public void perform(Player player, String[] args){
        if (args.length < 3){
            player.sendMessage("Not enough arguments");
            player.sendMessage("Correct Syntax: " + getSyntax());
        }
        else if(args.length == 3){
            PlayerDataClass receiver = PlayerStatStorage.findStatUUID(Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString());
            PlayerDataClass sender = PlayerStatStorage.findStatUUID(player.getUniqueId().toString());
            int amount = 0;
            if(!args[2].matches("[0*9]+")){
                player.sendMessage( "Invalid Syntax for the amount of money");
                return;
            }
            try{
                amount = Integer.parseInt(args[2]);

            } catch (NumberFormatException e) {
                player.sendMessage( "Invalid Syntax for the amount of money");
                throw new RuntimeException(e);
            }
            sender.removeFromBalance(amount);
            receiver.addToBalance(amount);
            player.sendMessage( "Paid "  + amount + " Ecu to " + receiver.getPlayerName());
            Bukkit.getOfflinePlayer(args[1]).getPlayer().sendMessage("Received "  + amount + " Ecu to " + receiver.getPlayerName());

        }
        else {
            player.sendMessage("Too many arguments");
            player.sendMessage("Correct Syntax: " + getSyntax());
        }
    }



}



