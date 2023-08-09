package org.tan.towns_and_nations.commands.debugsubcommands;

import org.bukkit.entity.Player;
import org.tan.towns_and_nations.DataClass.PlayerDataClass;
import org.tan.towns_and_nations.DataClass.TownDataClass;
import org.tan.towns_and_nations.commands.SubCommand;
import org.tan.towns_and_nations.storage.PlayerStatStorage;
import org.tan.towns_and_nations.storage.TownDataStorage;

import java.util.ArrayList;

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

    @Override
    public void perform(Player player, String[] args) {
        ArrayList<PlayerDataClass> stats = PlayerStatStorage.getStats();
        for (PlayerDataClass stat : stats) {
            String name = stat.getPlayerName();
            int balance = stat.getBalance();
            String townName;
            if (TownDataStorage.getTown(stat.getTownId()) != null) {
                townName = TownDataStorage.getTown(stat.getTownId()).getTownName();
            } else {
                townName = null;
            }

            player.sendMessage(name +
                    ": " + balance +
                    " ecu, town: " + townName);
        }
    }
}