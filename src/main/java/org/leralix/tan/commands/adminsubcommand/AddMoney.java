package org.leralix.tan.commands.adminsubcommand;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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
    public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args){
        return payPlayerSuggestion(args);
    }
    @Override
    public void perform(Player player, String[] args) {

        if (args.length < 2) {
            player.sendMessage(TanChatUtils.getTANString() + Lang.NOT_ENOUGH_ARGS_ERROR.get());
            player.sendMessage(TanChatUtils.getTANString() + Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));
        }
        else if (args.length == 3) {
            PlayerData target = PlayerDataStorage.get(Bukkit.getServer().getPlayer(args[1]));
            double amount;
            try {
                amount = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                player.sendMessage(TanChatUtils.getTANString() + Lang.SYNTAX_ERROR_AMOUNT.get());
                return;
            }
            EconomyUtil.addFromBalance(target, amount);
            player.sendMessage(TanChatUtils.getTANString() + Lang.ADD_MONEY_COMMAND_SUCCESS.get(amount,target.getName()));
            FileUtil.addLineToHistory(Lang.HISTORY_ADMIN_GIVE_MONEY.get(player.getName(),amount,target.getName()));
        }
        else{
            player.sendMessage(TanChatUtils.getTANString() + Lang.TOO_MANY_ARGS_ERROR.get());
            player.sendMessage(TanChatUtils.getTANString() + Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));
        }
    }
}