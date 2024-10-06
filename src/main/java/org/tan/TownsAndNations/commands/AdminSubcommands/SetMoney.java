package org.tan.TownsAndNations.commands.AdminSubcommands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.commands.SubCommand;
import org.tan.TownsAndNations.storage.DataStorage.PlayerDataStorage;
import org.tan.TownsAndNations.utils.FileUtil;

import java.util.List;

import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;

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
        return "/tandebug setmoney <player> <amount>";
    }
    public List<String> getTabCompleteSuggestions(Player player, String[] args){
        return payPlayerSuggestion(args);
    }



    @Override
    public void perform(Player player, String[] args) {

        if(TownsAndNations.hasExternalEconomy()){
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