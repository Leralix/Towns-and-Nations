package org.tan.TownsAndNations.commands.debugsubcommands;

import org.bukkit.entity.Player;
import org.tan.TownsAndNations.commands.SubCommand;
import org.tan.TownsAndNations.storage.*;

import java.util.List;

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
    public List<String> getTabCompleteSuggestions(Player player, String[] args){
        return null;
    }
    @Override
    public void perform(Player player, String[] args) {
        TownDataStorage.saveStats();
        ClaimedChunkStorage.saveStats();
        PlayerStatStorage.saveStats();
        player.sendMessage("All stats saved successfully!");
    }
}