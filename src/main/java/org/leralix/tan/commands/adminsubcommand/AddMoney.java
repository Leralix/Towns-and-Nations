package org.leralix.tan.commands.adminsubcommand;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.commands.SubCommand;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.FileUtil;

import java.util.List;

import static org.leralix.tan.utils.ChatUtils.getTANString;

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
            player.sendMessage(getTANString() + Lang.NOT_ENOUGH_ARGS_ERROR.get());
            player.sendMessage(getTANString() + Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));
        }
        else if (args.length == 3) {
            PlayerData target = PlayerDataStorage.get(Bukkit.getServer().getPlayer(args[1]));
            int amount;
            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                player.sendMessage(getTANString() + Lang.SYNTAX_ERROR_AMOUNT.get());
                return;
            }

            target.addToBalance(amount);
            player.sendMessage(getTANString() + Lang.ADD_MONEY_COMMAND_SUCCESS.get(amount,target.getName()));
            FileUtil.addLineToHistory(Lang.HISTORY_ADMIN_GIVE_MONEY.get(player.getName(),amount,target.getName()));
        }
        else{
            player.sendMessage(getTANString() + Lang.TOO_MANY_ARGS_ERROR.get());
            player.sendMessage(getTANString() + Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));
        }
    }
}