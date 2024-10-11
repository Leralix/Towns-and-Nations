package org.leralix.tan.commands.debugsubcommand;

import org.bukkit.entity.Player;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.tasks.DailyTasks;
import org.leralix.tan.commands.SubCommand;

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

