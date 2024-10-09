package org.leralix.tan.commands.debugsubcommand;

import org.bukkit.entity.Player;
import org.leralix.tan.commands.SubCommand;
import org.leralix.tan.utils.DropChances;

import java.util.Collections;
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
    public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args){
        return Collections.emptyList();
    }
    @Override
    public void perform(Player player, String[] args) {
        player.sendMessage("Drop chances: " + DropChances.getDropChances().keySet());


    }

}
