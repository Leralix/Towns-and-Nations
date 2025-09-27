package org.leralix.tan.commands.player;

import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.PlayerDataStorage;

import java.util.Collections;
import java.util.List;


public class SeeBalanceCommand extends PlayerSubCommand {
    @Override
    public String getName() {
        return "balance";
    }

    @Override
    public String getDescription() {
        return Lang.BAL_COMMAND_DESC.getDefault();
    }

    public int getArguments() {
        return 1;
    }

    @Override
    public String getSyntax() {
        return "/tan balance";
    }

    public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args) {
        return Collections.emptyList();
    }

    @Override
    public void perform(Player player, String[] args) {
        LangType langType = PlayerDataStorage.getInstance().get(player).getLang();
        if (args.length == 1) {
            player.sendMessage(Lang.BAL_AMOUNT.get(langType, Double.toString(EconomyUtil.getBalance(PlayerDataStorage.getInstance().get(player)))));
        } else if (args.length > 1) {
            player.sendMessage(Lang.TOO_MANY_ARGS_ERROR.get(langType));
            player.sendMessage(Lang.CORRECT_SYNTAX_INFO.get(langType, getSyntax()));
        }
    }

}



