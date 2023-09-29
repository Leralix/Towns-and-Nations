package org.tan.TownsAndNations.commands.debugsubcommands;


import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.commands.SubCommand;
import org.tan.TownsAndNations.storage.TownDataStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Update extends SubCommand {

    @Override
    public String getName() {
        return "update";
    }

    @Override
    public String getDescription() {
        return "Update the plugin";
    }

    @Override
    public int getArguments() {
        return 0;
    }

    @Override
    public String getSyntax() {
        return "/tandebug update";
    }
    public List<String> getTabCompleteSuggestions(Player player, String[] args){
        return null;
    }
    @Override
    public void perform(Player player, String[] args) {
        for(TownData townData : TownDataStorage.getTownList().values()){
            townData.update();
        }
    }
}