package org.tan.TownsAndNations.commands.debugsubcommands;


import org.bukkit.entity.Player;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.Tasks.DailyTasks;
import org.tan.TownsAndNations.commands.SubCommand;

import java.util.List;

import static org.tan.TownsAndNations.utils.ChatUtils.getTANDebugString;

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
        DailyTasks.ClearOldTaxes();
        player.sendMessage(getTANDebugString() + Lang.COMMAND_GENERIC_SUCCESS.get());
    }
}