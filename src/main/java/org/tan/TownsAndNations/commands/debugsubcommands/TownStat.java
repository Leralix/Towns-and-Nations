package org.tan.TownsAndNations.commands.debugsubcommands;


import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.TownDataClass;
import org.tan.TownsAndNations.commands.SubCommand;
import org.tan.TownsAndNations.storage.TownDataStorage;

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
        HashMap<String, TownDataClass> towns = TownDataStorage.getTownList();
        for (Map.Entry<String, TownDataClass> e : towns.entrySet()) {
            String key = e.getKey();
            TownDataClass value = e.getValue();
            player.sendMessage(key + ": " + value.getTownName());
        }
        player.sendMessage("Prochaine clef: " + TownDataStorage.getNewTownId());
    }
}