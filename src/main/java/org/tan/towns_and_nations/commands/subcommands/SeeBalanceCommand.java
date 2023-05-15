package org.tan.towns_and_nations.commands.subcommands;

import org.bukkit.entity.Player;
import org.tan.towns_and_nations.commands.SubCommand;
import org.tan.towns_and_nations.utils.PlayerStatStorage;


public class SeeBalanceCommand extends SubCommand  {
    @Override
    public String getName() {
        return "balance";
    }

    @Override
    public String getDescription() {
        return "Look at your balance";
    }

    @Override
    public String getSyntax() {
        return "/tan balance";
    }

    @Override
    public void perform(Player player, String[] args){
        if (args.length == 1){
            player.sendMessage("Your Balance: " + PlayerStatStorage.findStatUUID(player.getUniqueId().toString()).getBalance());
        }else if(args.length > 1){
            player.sendMessage("Too many arguments");
            player.sendMessage("Correct Syntax: /tan balance");
        }
    }

}



