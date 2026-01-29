package org.leralix.tan.commands.admin;

import org.leralix.lib.commands.CommandManager;
import org.leralix.lib.commands.MainHelpCommand;
import org.leralix.tan.storage.stored.PlayerDataStorage;

public class AdminCommandManager extends CommandManager {

    public AdminCommandManager(PlayerDataStorage playerDataStorage){
        super("tan.admin.commands");
        addSubCommand(new OpenAdminGUI());
        addSubCommand(new AddMoney(playerDataStorage));
        addSubCommand(new SetMoney(playerDataStorage));
        addSubCommand(new UnclaimAdminCommand());

        addSubCommand(new NameFilterAdminCommand());

        addSubCommand(new ReloadCommand());
        addSubCommand(new SudoPlayer());
        addSubCommand(new MainHelpCommand(this));
    }

    @Override
    public String getName() {
        return "tanadmin";
    }


}
