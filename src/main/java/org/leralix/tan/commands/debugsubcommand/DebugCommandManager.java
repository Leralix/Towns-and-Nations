package org.leralix.tan.commands.debugsubcommand;

import org.leralix.lib.commands.CommandManager;
import org.leralix.lib.commands.MainHelpCommand;

public class DebugCommandManager extends CommandManager {

    public DebugCommandManager(){

        addSubCommand(new SaveData());
        addSubCommand(new CreateBackup());
        addSubCommand(new ColorCode());

        addSubCommand(new SkipDay());
        addSubCommand(new PlaySound());
        addSubCommand(new ActionBarCommand());
        addSubCommand(new MainHelpCommand(this));
        addSubCommand(new SendReport());
    }

    @Override
    public String getName() {
        return "tandebug";
    }
}
