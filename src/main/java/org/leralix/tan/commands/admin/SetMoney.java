package org.leralix.tan.commands.admin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.leralix.lib.commands.SubCommand;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.FileUtil;
import org.leralix.tan.utils.TanChatUtils;

import java.util.List;

public class SetMoney extends SubCommand {

    @Override
    public String getName() {
        return "setmoney";
    }

    @Override
    public String getDescription() {
        return Lang.ADMIN_SET_PLAYER_MONEY_BALANCE.get();
    }

    @Override
    public int getArguments() {
        return 2;
    }

    @Override
    public String getSyntax() {
        return "/tanadmin setmoney <player> <amount>";
    }
    public List<String> getTabCompleteSuggestions(CommandSender player, String lowerCase, String[] args){
        return payPlayerSuggestion(args);
    }

    @Override
    public void perform(CommandSender player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(TanChatUtils.getTANString() + Lang.NOT_ENOUGH_ARGS_ERROR.get());
            player.sendMessage(TanChatUtils.getTANString() + Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));
        } else if (args.length == 3) {
            ITanPlayer target = PlayerDataStorage.getInstance().get(Bukkit.getOfflinePlayer(args[1]));
            setMoney(player, args, target);

        } else {
            player.sendMessage(TanChatUtils.getTANString() + Lang.TOO_MANY_ARGS_ERROR.get());
            player.sendMessage(TanChatUtils.getTANString() + Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));
        }
    }

    static void setMoney(CommandSender player, String[] args, ITanPlayer target) {
        double amount;
        try {
            amount = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            player.sendMessage(TanChatUtils.getTANString() + Lang.SYNTAX_ERROR_AMOUNT.get());
            return;
        }

        EconomyUtil.setBalance(target, amount);
        target.setBalance(amount);
        player.sendMessage(TanChatUtils.getTANString() + Lang.SET_MONEY_COMMAND_SUCCESS.get(amount, target.getNameStored()));
        FileUtil.addLineToHistory(Lang.HISTORY_ADMIN_SET_MONEY.get(player.getName(),amount, target.getNameStored()));
    }
}