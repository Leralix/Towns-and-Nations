package org.tan.TownsAndNations.commands.debugsubcommands;

import org.bukkit.entity.Player;
import org.tan.TownsAndNations.commands.SubCommand;

import java.util.List;

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

        player.sendMessage("Commande executée");
    }
}