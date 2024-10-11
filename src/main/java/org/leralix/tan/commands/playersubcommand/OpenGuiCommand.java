package org.leralix.tan.commands.playersubcommand;


import org.leralix.tan.gui.PlayerGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.commands.SubCommand;


import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

import static org.leralix.tan.utils.ChatUtils.getTANString;


public class OpenGuiCommand extends SubCommand  {
    @Override
    public String getName() {
        return "gui";
    }


    @Override
    public String getDescription() {
        return Lang.TOWN_GUI_COMMAND_DESC.get();
    }
    public int getArguments(){ return 2;}


    @Override
    public String getSyntax() {
        return "/tan gui";
    }

    @Override
    public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args){
        return Collections.emptyList();
    }
    @Override
    public void perform(Player player, String[] args){
        if (args.length == 1){

            getOpeningGui(player);
        }else if(args.length > 1){
            player.sendMessage(getTANString() + Lang.TOO_MANY_ARGS_ERROR.get());
            player.sendMessage(getTANString() + Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));
        }

    }

    private void getOpeningGui(Player player) {
        PlayerGUI.openMainMenu(player);
    }



}



