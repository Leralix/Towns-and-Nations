package org.leralix.tan.commands.playersubcommand;

import org.bukkit.entity.Player;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.commands.SubCommand;
import org.leralix.tan.storage.stored.PlayerDataStorage;

import java.util.Collections;
import java.util.List;

import static org.leralix.tan.utils.ChatUtils.getTANString;


public class SeeBalanceCommand extends SubCommand  {
    @Override
    public String getName() {
        return "balance";
    }

    @Override
    public String getDescription() {
        return Lang.BAL_COMMAND_DESC.get();
    }
    public int getArguments(){
        return 1;
    }

    @Override
    public String getSyntax() {
        return "/tan balance";
    }
    public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args){
        return Collections.emptyList();
    }

    @Override
    public void perform(Player player, String[] args){
        if (args.length == 1){
            player.sendMessage(getTANString() + Lang.BAL_AMOUNT.get(EconomyUtil.getBalance(PlayerDataStorage.get(player))));
        }
        else if(args.length > 1){
            player.sendMessage(getTANString() + Lang.TOO_MANY_ARGS_ERROR.get());
            player.sendMessage(getTANString() + Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));
        }
    }

}



