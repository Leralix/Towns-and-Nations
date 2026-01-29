package org.leralix.tan.commands.debug;

import org.leralix.lib.commands.CommandManager;
import org.leralix.lib.commands.MainHelpCommand;
import org.leralix.tan.tasks.SaveStats;

public class DebugCommandManager extends CommandManager {

    public DebugCommandManager(SaveStats saveStats){
        super("tan.admin.commands");

        addSubCommand(new SaveData(saveStats));
        addSubCommand(new CreateBackup());

        addSubCommand(new SkipDay());
        addSubCommand(new PlaySound());
        addSubCommand(new MainHelpCommand(this));
        addSubCommand(new SendReport());
    }

    @Override
    public String getName() {
        return "tandebug";
    }
}
