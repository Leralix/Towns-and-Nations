package org.tan.TownsAndNations.commands.debugsubcommands;

import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.TownDataClass;
import org.tan.TownsAndNations.DataClass.TownTreasury;
import org.tan.TownsAndNations.commands.SubCommand;
import org.tan.TownsAndNations.storage.TownDataStorage;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AddNewFeatures extends SubCommand {

    @Override
    public String getName() {
        return "addnewfeatures";
    }

    @Override
    public String getDescription() {
        return "Adds new features to towns.";
    }

    @Override
    public int getArguments() {
        return 0;
    }

    @Override
    public String getSyntax() {
        return "/tandebug addnewfeatures";
    }
    public List<String> getTabCompleteSuggestions(Player player, String[] args){
        return null;
    }
    @Override
    public void perform(Player player, String[] args) {
        LinkedHashMap<String, TownDataClass> towns = TownDataStorage.getTownList();
        for (Map.Entry<String, TownDataClass> e : towns.entrySet()) {
            TownDataClass townDataClass = e.getValue();
            townDataClass.setTreasury(new TownTreasury());
        }
        player.sendMessage("Commande executée");
    }
}