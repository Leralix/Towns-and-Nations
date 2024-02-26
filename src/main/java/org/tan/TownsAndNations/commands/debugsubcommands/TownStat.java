package org.tan.TownsAndNations.commands.debugsubcommands;


import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.commands.SubCommand;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TownStat extends SubCommand {

    @Override
    public String getName() {
        return "townstats";
    }

    @Override
    public String getDescription() {
        return "Lists all towns and their respective names.";
    }

    @Override
    public int getArguments() {
        return 0;
    }

    @Override
    public String getSyntax() {
        return "/tandebug townstats";
    }
    public List<String> getTabCompleteSuggestions(Player player, String[] args){
        return null;
    }
    @Override
    public void perform(Player player, String[] args) {
        player.sendMessage("Liste des villes:");
        HashMap<String, TownData> towns = TownDataStorage.getTownMap();
        for (Map.Entry<String, TownData> e : towns.entrySet()) {
            String key = e.getKey();
            TownData value = e.getValue();
            player.sendMessage(key + ": " + value.getName());
        }
        player.sendMessage("Prochaine clef: " + TownDataStorage.getNewTownId());
    }
}