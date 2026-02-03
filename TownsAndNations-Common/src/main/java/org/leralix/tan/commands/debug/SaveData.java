package org.leralix.tan.commands.debug;

import org.bukkit.command.CommandSender;
import org.leralix.lib.commands.SubCommand;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.tasks.SaveStats;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.Collections;
import java.util.List;

public class SaveData extends SubCommand {

    private final SaveStats saveStats;

    public SaveData(SaveStats saveStats) {
        this.saveStats = saveStats;
    }

    @Override
    public String getName() {
        return "saveall";
    }

    @Override
    public String getDescription() {
        return Lang.DEBUG_SAVE_ALL_DATA.getDefault();
    }

    @Override
    public int getArguments() {
        return 0;
    }

    @Override
    public String getSyntax() {
        return "/tandebug saveall";
    }

    public List<String> getTabCompleteSuggestions(CommandSender commandSender, String lowerCase, String[] args) {
        return Collections.emptyList();
    }

    @Override
    public void perform(CommandSender commandSender, String[] args) {
        saveStats.saveAll();
        TanChatUtils.message(commandSender, Lang.COMMAND_GENERIC_SUCCESS);
    }
}