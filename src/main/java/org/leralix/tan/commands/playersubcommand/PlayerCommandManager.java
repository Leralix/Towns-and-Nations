package org.leralix.tan.commands.playersubcommand;

import org.leralix.lib.commands.CommandManager;
import org.leralix.lib.commands.MainHelpCommand;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;

public class PlayerCommandManager extends CommandManager {

    public PlayerCommandManager(){
        addSubCommand(new InvitePlayerCommand());
        addSubCommand(new JoinTownCommand()); //hidden
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


        if(ConfigUtil.getCustomConfig(ConfigTag.TAN).getBoolean("AllowSellRareRessourcesByCommand",true)){
            addSubCommand(new SellRareItem());
        }
    }

    @Override
    public String getName() {
        return "tan";
    }

}
