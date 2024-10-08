package org.tan.TownsAndNations.commands.debugSubcommands;

import org.bukkit.entity.Player;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.Tasks.DailyTasks;
import org.tan.TownsAndNations.commands.SubCommand;

import java.util.List;

public class SkipDay extends SubCommand {

    @Override
    public String getName() {
        return "skipday";
    }

    @Override
    public String getDescription() {
        return Lang.DEBUG_SKIP_DAY.get();
    }

    @Override
    public int getArguments() {
        return 0;
    }

    @Override
    public String getSyntax() {
        return "/tandebug skipday";
    }
    public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args){
        return null;
    }
    @Override
    public void perform(Player player, String[] args) {

        DailyTasks.executeMidnightTasks();
    }
}

