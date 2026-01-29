package org.leralix.tan.commands.player;

import org.leralix.lib.commands.CommandManager;
import org.leralix.lib.commands.MainHelpCommand;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.constants.Constants;

public class PlayerCommandManager extends CommandManager {

    public PlayerCommandManager(PlayerDataStorage playerDataStorage){
        super("tan.base.commands");
        addSubCommand(new InvitePlayerCommand(playerDataStorage));
        addSubCommand(new JoinTownCommand(playerDataStorage));
        addSubCommand(new ClaimCommand(playerDataStorage));
        addSubCommand(new ClaimAreaCommand(playerDataStorage));
        addSubCommand(new EnableBoundaryCommand(playerDataStorage));
        addSubCommand(new UnclaimCommand(playerDataStorage));
        addSubCommand(new MapCommand(playerDataStorage));
        addSubCommand(new SeeBalanceCommand(playerDataStorage));
        addSubCommand(new PayCommand(Constants.getMaxPayRange(), playerDataStorage));
        addSubCommand(new OpenGuiCommand(playerDataStorage));
        addSubCommand(new OpenNewsletterCommand(playerDataStorage));
        addSubCommand(new ChannelChatScopeCommand(playerDataStorage));
        addSubCommand(new AutoClaimCommand());
        addSubCommand(new TownSpawnCommand(playerDataStorage));
        addSubCommand(new SetTownSpawnCommand(playerDataStorage));
        addSubCommand(new MainHelpCommand(this));
    }

    @Override
    public String getName() {
        return "tan";
    }

}
