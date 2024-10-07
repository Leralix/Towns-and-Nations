package org.tan.TownsAndNations.commands.debugSubcommands;

import org.tan.TownsAndNations.commands.CommandManager;
import org.tan.TownsAndNations.commands.TanHelpCommand;

public class DebugCommandManager extends CommandManager {

    public DebugCommandManager(){

        addSubCommand(new SaveData());
        addSubCommand(new CreateBackup());
        addSubCommand(new ColorCode());

        addSubCommand(new SkipDay());
        addSubCommand(new PlaySound());
        addSubCommand(new ActionBarCommand());
        addSubCommand(new GetDropChances());
        addSubCommand(new TanHelpCommand(this));

    }

    @Override
    public String getName() {
        return "tandebug";
    }
}
