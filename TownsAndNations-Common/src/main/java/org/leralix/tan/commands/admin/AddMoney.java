package org.leralix.tan.commands.admin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.leralix.lib.commands.SubCommand;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.file.FileUtil;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.List;

public class AddMoney extends SubCommand {

    @Override
    public String getName() {
        return "addmoney";
    }

    @Override
    public String getDescription() {
        return Lang.ADMIN_ADD_MONEY_TO_PLAYER.getDefault();
    }

    @Override
    public int getArguments() {
        return 2;
    }

    @Override
    public String getSyntax() {
        return "/tanadmin addmoney <player> <amount>";
    }

    public List<String> getTabCompleteSuggestions(CommandSender commandSender, String lowerCase, String[] args) {
        return payPlayerSuggestion(args);
    }

    @Override
    public void perform(CommandSender commandSender, String[] args) {

        if (args.length < 2) {
            TanChatUtils.message(commandSender, Lang.NOT_ENOUGH_ARGS_ERROR, SoundEnum.NOT_ALLOWED);
            TanChatUtils.message(commandSender, Lang.CORRECT_SYNTAX_INFO);
        } else if (args.length == 3) {
            ITanPlayer target = PlayerDataStorage.getInstance().get(Bukkit.getServer().getOfflinePlayer(args[1]));
            addMoney(commandSender, args, target);
        } else {
            TanChatUtils.message(commandSender, Lang.TOO_MANY_ARGS_ERROR, SoundEnum.NOT_ALLOWED);
            TanChatUtils.message(commandSender, Lang.CORRECT_SYNTAX_INFO);
        }
    }

    static void addMoney(CommandSender commandSender, String[] args, ITanPlayer target) {
        double amount;
        try {
            amount = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            TanChatUtils.message(commandSender, Lang.SYNTAX_ERROR_AMOUNT);
            return;
        }
        EconomyUtil.addFromBalance(target, amount);
        TanChatUtils.message(commandSender, Lang.ADD_MONEY_COMMAND_SUCCESS.get(Double.toString(amount), target.getNameStored()));
        FileUtil.addLineToHistory(Lang.HISTORY_ADMIN_GIVE_MONEY.get(commandSender.getName(), Double.toString(amount), target.getNameStored()));
    }
}