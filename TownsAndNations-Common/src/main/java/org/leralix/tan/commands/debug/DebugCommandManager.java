package org.leralix.tan.commands.debug;

import org.leralix.lib.commands.CommandManager;
import org.leralix.lib.commands.MainHelpCommand;

public class DebugCommandManager extends CommandManager {

    public DebugCommandManager(){
        super("tan.admin.commands");

        addSubCommand(new SaveData());
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
