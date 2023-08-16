package org.tan.towns_and_nations.commands.debugsubcommands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.tan.towns_and_nations.DataClass.PlayerDataClass;
import org.tan.towns_and_nations.commands.SubCommand;
import org.tan.towns_and_nations.storage.PlayerStatStorage;

import java.util.ArrayList;
import java.util.List;

public class AddMoney extends SubCommand {

    @Override
    public String getName() {
        return "addmoney";
    }

    @Override
    public String getDescription() {
        return "Add money to a player.";
    }

    @Override
    public int getArguments() {
        return 2; // expecting player name and amount
    }

    @Override
    public String getSyntax() {
        return "/tandebug addmoney <player> <amount>";
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
            int amount = 0;
            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                player.sendMessage("Invalid Syntax for the amount of money");
                return;
            }

            target.addToBalance(amount);
            player.sendMessage("Added " + amount + " Ecu to " + target.getPlayerName());
        } else {
            player.sendMessage("Too many arguments");
        }
    }
}