package org.tan.TownsAndNations.commands.adminSubcommands;

import org.bukkit.entity.Player;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.commands.SubCommand;
import org.tan.TownsAndNations.utils.config.ConfigTag;
import org.tan.TownsAndNations.utils.config.ConfigUtil;

import java.util.List;

import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;

public class reloadCommand extends SubCommand {
    @Override
    public String getName() {
        return "reload";
    }


    @Override
    public String getDescription() {
        return Lang.ADMIN_RELOAD_COMMAND.get();
    }
    public int getArguments(){ return 1;}


    @Override
    public String getSyntax() {
        return "/tanadmin reload";
    }

    @Override
    public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args){
        return null;
    }
    @Override
    public void perform(Player player, String[] args){
        if (args.length == 1){
            ConfigUtil.addCustomConfig("config.yml", ConfigTag.MAIN);
            ConfigUtil.addCustomConfig("townUpgrades.yml", ConfigTag.UPGRADES);
            player.sendMessage(getTANString() + Lang.RELOAD_SUCCESS.get());
        }else{
            player.sendMessage(getTANString() + Lang.TOO_MANY_ARGS_ERROR.get());
            player.sendMessage(getTANString() + Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));
        }
    }

}
