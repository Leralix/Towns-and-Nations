package org.leralix.tan.commands.adminsubcommand;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.leralix.lib.commands.SubCommand;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.TanChatUtils;
import org.leralix.tan.utils.FileUtil;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.lang.Lang;

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
            PlayerData target = PlayerDataStorage.getInstance().get(Bukkit.getOfflinePlayer(args[1]));
            double amount;
            try {
                amount = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                player.sendMessage(TanChatUtils.getTANString() + Lang.SYNTAX_ERROR_AMOUNT.get());
                return;
            }

            EconomyUtil.setBalance(target, amount);
            target.setBalance(amount);
            player.sendMessage(TanChatUtils.getTANString() + Lang.SET_MONEY_COMMAND_SUCCESS.get(amount,target.getNameStored()));
            FileUtil.addLineToHistory(Lang.HISTORY_ADMIN_SET_MONEY.get(player.getName(),amount,target.getNameStored()));

        } else {
            player.sendMessage(TanChatUtils.getTANString() + Lang.TOO_MANY_ARGS_ERROR.get());
            player.sendMessage(TanChatUtils.getTANString() + Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));
        }
    }
}