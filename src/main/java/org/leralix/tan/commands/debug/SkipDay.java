package org.leralix.tan.commands.debug;

import org.bukkit.command.CommandSender;
import org.leralix.lib.commands.SubCommand;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.tasks.DailyTasks;

import java.util.Collections;
import java.util.List;

public class SkipDay extends SubCommand {

    @Override
    public String getName() {
        return "skipday";
    }

    @Override
    public String getDescription() {
        return Lang.DEBUG_SKIP_DAY.getDefault();
    }

    @Override
    public int getArguments() {
        return 0;
    }

    @Override
    public String getSyntax() {
        return "/tandebug skipday";
    }
    public List<String> getTabCompleteSuggestions(CommandSender commandSender, String lowerCase, String[] args){
        return Collections.emptyList();
    }
    @Override
    public void perform(CommandSender commandSender, String[] args) {
        DailyTasks.executeMidnightTasks();
        commandSender.sendMessage(Lang.COMMAND_GENERIC_SUCCESS.getDefault());
    }
}

