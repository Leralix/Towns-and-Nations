package org.tan.TownsAndNations.commands.adminSubcommands;

import org.tan.TownsAndNations.commands.CommandManager;
import org.tan.TownsAndNations.commands.TanHelpCommand;

public class AdminCommandManager extends CommandManager {

    public AdminCommandManager(){
        addSubCommand(new AddMoney());
        addSubCommand(new SetMoney());
        addSubCommand(new SpawnVillager());
        addSubCommand(new getRareItem());

        addSubCommand(new reloadCommand());
        addSubCommand(new UnclaimAdminCommand());
        addSubCommand(new OpenAdminGUI());
        addSubCommand(new SudoPlayer());
        addSubCommand(new TanHelpCommand(this));
    }

    @Override
    public String getName() {
        return "tanadmin";
    }


}
