package org.tan.TownsAndNations.commands.debugsubcommands;

import org.bukkit.entity.Player;
import org.tan.TownsAndNations.commands.SubCommand;
import org.tan.TownsAndNations.utils.DropChances;

import java.util.List;

public class GetDropChances extends SubCommand {

    @Override
    public String getName() {
        return "dropchances";
    }

    @Override
    public String getDescription() {
        return "show rare items drop chances";
    }

    @Override
    public int getArguments() {
        return 0;
    }

    @Override
    public String getSyntax() {
        return "/tandebug dropchances";
    }
    public List<String> getTabCompleteSuggestions(Player player, String[] args){
        return null;
    }
    @Override
    public void perform(Player player, String[] args) {
        player.sendMessage("Drop chances: " + DropChances.getDropChances().keySet());


    }

}
