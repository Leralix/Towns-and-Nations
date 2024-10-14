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
    public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args){
        return payPlayerSuggestion(args);
    }



    @Override
    public void perform(Player player, String[] args) {

        if(EconomyUtil.hasExternalEconomy()){
            player.sendMessage(getTANString() + Lang.ECONOMY_EXISTS.get());
            return;
        }

        if (args.length < 3) {
            player.sendMessage("Not enough arguments");
        } else if (args.length == 3) {
            PlayerData target = PlayerDataStorage.get(Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString());
            int amount;
            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                player.sendMessage("Invalid Syntax for the amount of money");
                return;
            }

            target.setBalance(amount);
            player.sendMessage("Set Balance of "+ target.getName() + " to " + amount);
            FileUtil.addLineToHistory(Lang.HISTORY_ADMIN_SET_MONEY.get(player.getName(),amount,target.getName()));

        } else {
            player.sendMessage("Too many arguments");
        }
    }
}