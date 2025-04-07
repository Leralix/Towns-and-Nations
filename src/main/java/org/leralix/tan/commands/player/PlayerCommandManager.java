package org.leralix.tan.commands.player;

import org.leralix.lib.commands.CommandManager;
import org.leralix.lib.commands.MainHelpCommand;

public class PlayerCommandManager extends CommandManager {

    public PlayerCommandManager(){
        super("tan.base.commands");
        addSubCommand(new InvitePlayerCommand());
        addSubCommand(new JoinTownCommand());
        addSubCommand(new ClaimCommand());
        addSubCommand(new UnclaimCommand());
        addSubCommand(new MapCommand());
        addSubCommand(new SeeBalanceCommand());
        addSubCommand(new PayCommand());
        addSubCommand(new OpenGuiCommand());
        addSubCommand(new OpenNewsletterCommand());
        addSubCommand(new ChannelChatScopeCommand());
        addSubCommand(new AutoClaimCommand());
        addSubCommand(new TownSpawnCommand());
        addSubCommand(new SetTownSpawnCommand());
        addSubCommand(new MainHelpCommand(this));
    }

    @Override
    public String getName() {
        return "tan";
    }

}
