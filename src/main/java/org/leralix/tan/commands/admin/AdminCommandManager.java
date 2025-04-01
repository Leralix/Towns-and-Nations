package org.leralix.tan.commands.admin;

import org.leralix.lib.commands.CommandManager;
import org.leralix.lib.commands.MainHelpCommand;

public class AdminCommandManager extends CommandManager {

    public AdminCommandManager(){
        super("tan.admin.commands");
        addSubCommand(new OpenAdminGUI());
        addSubCommand(new AddMoney());
        addSubCommand(new SetMoney());

        addSubCommand(new ReloadCommand());
        addSubCommand(new SudoPlayer());
        addSubCommand(new MainHelpCommand(this));
    }

    @Override
    public String getName() {
        return "tanadmin";
    }


}
