package org.leralix.tan.commands.server;

import org.leralix.lib.commands.CommandManager;
import org.leralix.tan.storage.stored.PlayerDataStorage;

public class ServerCommandManager extends CommandManager {

    public ServerCommandManager(PlayerDataStorage playerDataStorage){
        super("tan.server.commands");
        addSubCommand(new CreateTownServer(playerDataStorage));
        addSubCommand(new ApplyTownServer(playerDataStorage));
        addSubCommand(new QuitTownServer(playerDataStorage));
        addSubCommand(new DisbandTownServer(playerDataStorage));
        addSubCommand(new OpenGuiServer());
        addSubCommand(new LandmarkUpdateServer());
        addSubCommand(new LandmarkSetStoredLimitServer());
    }

    @Override
    public String getName() {
        return "tanserver";
    }


}
