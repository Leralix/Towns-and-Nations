package org.leralix.tan.commands.admin;

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

public class AddMoney extends SubCommand {

    @Override
    public String getName() {
        return "addmoney";
    }

    @Override
    public String getDescription() {
        return Lang.ADMIN_ADD_MONEY_TO_PLAYER.get();
    }

    @Override
    public int getArguments() {
        return 2;
    }

    @Override
    public String getSyntax() {
        return "/tanadmin addmoney <player> <amount>";
    }
    public List<String> getTabCompleteSuggestions(CommandSender commandSender, String lowerCase, String[] args){
        return payPlayerSuggestion(args);
    }
    @Override
    public void perform(CommandSender commandSender, String[] args) {

        if (args.length < 2) {
            commandSender.sendMessage(TanChatUtils.getTANString() + Lang.NOT_ENOUGH_ARGS_ERROR.get());
            commandSender.sendMessage(TanChatUtils.getTANString() + Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));
        }
        else if (args.length == 3) {
            PlayerData target = PlayerDataStorage.getInstance().get(Bukkit.getServer().getOfflinePlayer(args[1]));
            addMoney(commandSender, args, target);
        }
        else{
            commandSender.sendMessage(TanChatUtils.getTANString() + Lang.TOO_MANY_ARGS_ERROR.get());
            commandSender.sendMessage(TanChatUtils.getTANString() + Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));
        }
    }

    static void addMoney(CommandSender commandSender, String[] args, PlayerData target) {
        double amount;
        try {
            amount = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            commandSender.sendMessage(TanChatUtils.getTANString() + Lang.SYNTAX_ERROR_AMOUNT.get());
            return;
        }
        EconomyUtil.addFromBalance(target, amount);
        commandSender.sendMessage(TanChatUtils.getTANString() + Lang.ADD_MONEY_COMMAND_SUCCESS.get(amount, target.getNameStored()));
        FileUtil.addLineToHistory(Lang.HISTORY_ADMIN_GIVE_MONEY.get(commandSender.getName(),amount, target.getNameStored()));
    }
}