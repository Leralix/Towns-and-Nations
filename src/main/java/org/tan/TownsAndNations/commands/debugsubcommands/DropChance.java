package org.tan.TownsAndNations.commands.debugsubcommands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.commands.SubCommand;
import org.tan.TownsAndNations.utils.DropChances;
import org.tan.TownsAndNations.DataClass.RareItem;

import java.util.List;
import java.util.Map;

public class DropChance extends SubCommand {

    @Override
    public String getName() {
        return "dropchance";
    }

    @Override
    public String getDescription() {
        return "get the rare item drop chances";
    }

    @Override
    public int getArguments() {
        return 0;
    }

    @Override
    public String getSyntax() {
        return "/tandebug dropchance";
    }
    public List<String> getTabCompleteSuggestions(Player player, String[] args){
        return null;
    }
    @Override
    public void perform(Player player, String[] args) {

        Map<String, RareItem> dropChances = DropChances.getDropChances();

        for (Map.Entry<String, RareItem> entry : dropChances.entrySet()) {
            String key = entry.getKey();
            RareItem value = entry.getValue();

            player.sendMessage("Key: " + key + ", Value: " + value.getDropChance());
        }
    }


}
