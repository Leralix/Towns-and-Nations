package org.leralix.tan.commands.server;

import org.leralix.lib.commands.CommandManager;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;

public class ServerCommandManager extends CommandManager {

    public ServerCommandManager(PlayerDataStorage playerDataStorage, TownDataStorage townDataStorage){
        super("tan.server.commands");
        addSubCommand(new CreateTownServer(playerDataStorage, townDataStorage));
        addSubCommand(new ApplyTownServer(playerDataStorage, townDataStorage));
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
