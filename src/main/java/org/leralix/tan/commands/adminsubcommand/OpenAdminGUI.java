package org.leralix.tan.commands.adminsubcommand;


import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.tan.utils.TanChatUtils;
import org.leralix.tan.lang.Lang;

import java.util.Collections;
import java.util.List;

import static org.leralix.tan.gui.AdminGUI.openMainMenu;


public class OpenAdminGUI extends PlayerSubCommand {
    @Override
    public String getName() {
        return "gui";
    }


    @Override
    public String getDescription() {
        return Lang.ADMIN_OPEN_GUI.get();
    }
    public int getArguments(){ return 2;}


    @Override
    public String getSyntax() {
        return "/tanadmin gui";
    }

    @Override
    public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args){
        return Collections.emptyList();
    }
    @Override
    public void perform(Player player, String[] args){
        if (args.length == 1){
            openMainMenu(player);
        }else if(args.length > 1){
            player.sendMessage(TanChatUtils.getTANString() + Lang.TOO_MANY_ARGS_ERROR.get());
            player.sendMessage(TanChatUtils.getTANString() + Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));
        }

    }

}



