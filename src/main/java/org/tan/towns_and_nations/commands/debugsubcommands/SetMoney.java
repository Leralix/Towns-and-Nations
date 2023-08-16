package org.tan.towns_and_nations.commands.debugsubcommands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.tan.towns_and_nations.DataClass.PlayerDataClass;
import org.tan.towns_and_nations.commands.SubCommand;
import org.tan.towns_and_nations.storage.*;

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
            PlayerDataClass target = PlayerStatStorage.getStat(Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString());
            int amount;
            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                player.sendMessage("Invalid Syntax for the amount of money");
                return;
            }

            target.setBalance(amount);
            player.sendMessage("Set Balance of "+ target.getPlayerName() + " to " + amount);
        } else {
            player.sendMessage("Too many arguments");
        }
    }
}