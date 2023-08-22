package org.tan.TownsAndNations.commands.debugsubcommands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.commands.SubCommand;
import org.tan.TownsAndNations.storage.*;

import java.util.ArrayList;
import java.util.List;

public class SetMoney extends SubCommand {

    @Override
    public String getName() {
        return "setmoney";
    }

    @Override
    public String getDescription() {
        return "Set a player's money balance.";
    }

    @Override
    public int getArguments() {
        return 2; // expecting player name and amount
    }

    @Override
    public String getSyntax() {
        return "/tandebug setmoney <player> <amount>";
    }
    public List<String> getTabCompleteSuggestions(Player player, String[] args){
        List<String> suggestions = new ArrayList<>();
        if (args.length == 2) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                suggestions.add(p.getName());
            }
        }
        if (args.length == 3) {
            suggestions.add("<amount>");
        }
        return suggestions;
    }
    @Override
    public void perform(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("Not enough arguments");
        } else if (args.length == 3) {
            PlayerData target = PlayerDataStorage.getStat(Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString());
            int amount;
            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                player.sendMessage("Invalid Syntax for the amount of money");
                return;
            }

            target.setBalance(amount);
            player.sendMessage("Set Balance of "+ target.getName() + " to " + amount);
        } else {
            player.sendMessage("Too many arguments");
        }
    }
}