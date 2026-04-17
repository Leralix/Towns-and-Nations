package org.leralix.tan.commands.player;

import org.leralix.lib.commands.CommandManager;
import org.leralix.lib.commands.MainHelpCommand;
import org.leralix.tan.storage.LocalChatStorage;
import org.leralix.tan.storage.stored.NationStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionStorage;
import org.leralix.tan.storage.stored.TownStorage;
import org.leralix.tan.utils.constants.Constants;

public class PlayerCommandManager extends CommandManager {

    public PlayerCommandManager(PlayerDataStorage playerDataStorage, TownStorage townStorage, RegionStorage regionStorage, NationStorage nationStorage, LocalChatStorage localChatStorage){
        super("tan.base.commands");
        addSubCommand(new InvitePlayerCommand(playerDataStorage, townStorage));
        addSubCommand(new JoinTownCommand(playerDataStorage, townStorage));
        addSubCommand(new ClaimCommand(playerDataStorage));
        addSubCommand(new ClaimAreaCommand(playerDataStorage));
        addSubCommand(new EnableBoundaryCommand(playerDataStorage));
        addSubCommand(new UnclaimCommand(playerDataStorage));
        addSubCommand(new MapCommand(playerDataStorage));
        addSubCommand(new SeeBalanceCommand(playerDataStorage));
        addSubCommand(new PayCommand(Constants.getMaxPayRange(), playerDataStorage));
        addSubCommand(new OpenGuiCommand(playerDataStorage));
        addSubCommand(new OpenNewsletterCommand(playerDataStorage));
        addSubCommand(new ChannelChatScopeCommand(playerDataStorage, localChatStorage));
        addSubCommand(new AutoClaimCommand(playerDataStorage));
        addSubCommand(new SpawnCommand(playerDataStorage, townStorage, regionStorage, nationStorage));
        addSubCommand(new SetSpawnCommand(playerDataStorage, townStorage, regionStorage, nationStorage));
        addSubCommand(new MainHelpCommand(this));
    }

    @Override
    public String getName() {
        return "tan";
    }

}
