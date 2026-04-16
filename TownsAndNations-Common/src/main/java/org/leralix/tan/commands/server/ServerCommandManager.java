package org.leralix.tan.commands.server;

import org.leralix.lib.commands.CommandManager;
import org.leralix.tan.storage.stored.LandmarkStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownStorage;

public class ServerCommandManager extends CommandManager {

    public ServerCommandManager(PlayerDataStorage playerDataStorage, TownStorage townStorage, LandmarkStorage landmarkStorage){
        super("tan.server.commands");
        addSubCommand(new CreateTownServer(playerDataStorage, townStorage));
        addSubCommand(new ApplyTownServer(playerDataStorage, townStorage));
        addSubCommand(new QuitTownServer(playerDataStorage));
        addSubCommand(new DisbandTownServer(playerDataStorage));
        addSubCommand(new OpenGuiServer());
        addSubCommand(new LandmarkUpdateServer(landmarkStorage));
        addSubCommand(new LandmarkSetStoredLimitServer(landmarkStorage));
    }

    @Override
    public String getName() {
        return "tanserver";
    }


}
