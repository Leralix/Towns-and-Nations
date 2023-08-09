package org.tan.towns_and_nations.commands.debugsubcommands;

import org.bukkit.entity.Player;
import org.tan.towns_and_nations.commands.SubCommand;
import org.tan.towns_and_nations.storage.*;

public class SaveAll extends SubCommand {

    @Override
    public String getName() {
        return "saveall";
    }

    @Override
    public String getDescription() {
        return "Saves all the stats";
    }

    @Override
    public int getArguments() {
        return 0;
    }

    @Override
    public String getSyntax() {
        return "/tandebug saveall";
    }

    @Override
    public void perform(Player player, String[] args) {
        TownDataStorage.saveStats();
        ClaimedChunkStorage.saveStats();
        PlayerStatStorage.saveStats();
        player.sendMessage("All stats saved successfully!");
    }
}