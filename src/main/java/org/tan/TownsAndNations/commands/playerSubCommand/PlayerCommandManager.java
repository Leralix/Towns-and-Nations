package org.tan.TownsAndNations.commands.playerSubCommand;

import org.tan.TownsAndNations.commands.CommandManager;
import org.tan.TownsAndNations.commands.TanHelpCommand;
import org.tan.TownsAndNations.utils.config.ConfigTag;
import org.tan.TownsAndNations.utils.config.ConfigUtil;

public class PlayerCommandManager extends CommandManager {

    public PlayerCommandManager(){
        addSubCommand(new InvitePlayerCommand());
        addSubCommand(new JoinTownCommand());
        addSubCommand(new ClaimCommand());
        addSubCommand(new UnclaimCommand());
        addSubCommand(new MapCommand());
        addSubCommand(new SeeBalanceCommand());
        addSubCommand(new PayCommand());
        addSubCommand(new OpenGuiCommand());

        addSubCommand(new AcceptRelationCommand());
        addSubCommand(new ChannelChatScopeCommand());
        addSubCommand(new AcceptRegionRelationCommand());
        addSubCommand(new AutoClaimCommand());
        addSubCommand(new TownSpawnCommand());
        addSubCommand(new SetTownSpawnCommand());
        addSubCommand(new TanHelpCommand(this));


        if(ConfigUtil.getCustomConfig(ConfigTag.MAIN).getBoolean("AllowSellRareRessourcesByCommand",true)){
            addSubCommand(new SellRareItem());
        }
    }

    @Override
    public String getName() {
        return "tan";
    }

}
