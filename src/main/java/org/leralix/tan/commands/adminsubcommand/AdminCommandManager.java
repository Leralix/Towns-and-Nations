package org.leralix.tan.commands.adminsubcommand;

import org.leralix.tan.commands.CommandManager;
import org.leralix.tan.commands.TanHelpCommand;

public class AdminCommandManager extends CommandManager {

    public AdminCommandManager(){
        addSubCommand(new AddMoney());
        addSubCommand(new SetMoney());
        addSubCommand(new SpawnVillager());
        addSubCommand(new GetRareItem());

        addSubCommand(new ReloadCommand());
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
