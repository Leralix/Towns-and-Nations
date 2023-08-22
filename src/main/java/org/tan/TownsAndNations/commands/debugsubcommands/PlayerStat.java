package org.tan.TownsAndNations.commands.debugsubcommands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.commands.SubCommand;
import org.tan.TownsAndNations.storage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.TownDataStorage;

import java.util.ArrayList;
import java.util.List;

public class PlayerStat extends SubCommand {

    @Override
    public String getName() {
        return "playerstats";
    }

    @Override
    public String getDescription() {
        return "Displays stats for all players.";
    }

    @Override
    public int getArguments() {
        return 0;
    }

    @Override
    public String getSyntax() {
        return "/tandebug playerstats";
    }
    public List<String> getTabCompleteSuggestions(Player player, String[] args){
        List<String> suggestions = new ArrayList<>();
        if (args.length == 2) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                suggestions.add(p.getName());
            }
        }
        return suggestions;
    }
    @Override
    public void perform(Player player, String[] args) {
        List<PlayerData> stats = PlayerDataStorage.getStats();
        for (PlayerData stat : stats) {
            String name = stat.getName();
            int balance = stat.getBalance();
            String townName;
            if (TownDataStorage.getTown(stat.getTownId()) != null) {
                townName = TownDataStorage.getTown(stat.getTownId()).getName();
            } else {
                townName = null;
            }

            player.sendMessage(name +
                    ": " + balance +
                    " ecu, town: " + townName);
        }
    }
}