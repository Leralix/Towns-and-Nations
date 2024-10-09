package org.leralix.tan.commands.adminsubcommand;

import org.bukkit.entity.Player;
import org.leralix.tan.Lang.Lang;
import org.leralix.tan.commands.SubCommand;
import org.leralix.tan.utils.config.ConfigTag;
import org.leralix.tan.utils.config.ConfigUtil;

import java.util.Collections;
import java.util.List;

import static org.leralix.tan.utils.ChatUtils.getTANString;

public class ReloadCommand extends SubCommand {
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
        return Collections.emptyList();
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
