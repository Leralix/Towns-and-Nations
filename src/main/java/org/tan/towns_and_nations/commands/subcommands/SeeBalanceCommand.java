package org.tan.towns_and_nations.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.tan.towns_and_nations.Lang.Lang;
import org.tan.towns_and_nations.commands.SubCommand;
import org.tan.towns_and_nations.storage.PlayerStatStorage;

import java.util.ArrayList;
import java.util.List;

import static org.tan.towns_and_nations.utils.ChatUtils.getTANString;


public class SeeBalanceCommand extends SubCommand  {
    @Override
    public String getName() {
        return "balance";
    }

    @Override
    public String getDescription() {
        return Lang.BAL_COMMAND_DESC.getTranslation();
    }
    public int getArguments(){
        return 1;
    }

    @Override
    public String getSyntax() {
        return "/tan balance";
    }
    public List<String> getTabCompleteSuggestions(Player player, String[] args){
        return null;
    }

    @Override
    public void perform(Player player, String[] args){
        if (args.length == 1){
            player.sendMessage(getTANString() + Lang.BAL_AMOUNT.getTranslation(PlayerStatStorage.getStat(player).getBalance()));

        }else if(args.length > 1){
            player.sendMessage(getTANString() + Lang.TOO_MANY_ARGS_ERROR.getTranslation());
            player.sendMessage(getTANString() + Lang.CORRECT_SYNTAX_INFO.getTranslation(getSyntax()));
        }
    }

}



