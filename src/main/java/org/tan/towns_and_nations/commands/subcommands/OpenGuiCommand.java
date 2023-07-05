package org.tan.towns_and_nations.commands.subcommands;


import org.bukkit.ChatColor;
import org.tan.towns_and_nations.GUI.GuiManager2;
import org.tan.towns_and_nations.Lang.Lang;
import org.tan.towns_and_nations.commands.SubCommand;


import org.bukkit.entity.Player;

import static org.tan.towns_and_nations.utils.ChatUtils.getTANString;


public class OpenGuiCommand extends SubCommand  {
    @Override
    public String getName() {
        return "gui";
    }


    @Override
    public String getDescription() {
        return Lang.TOWN_GUI_COMMAND_DESC.getTranslation();
    }
    public int getArguments(){ return 2;}


    @Override
    public String getSyntax() {
        return "/tan gui";
    }

    @Override
    public void perform(Player player, String[] args){
        if (args.length == 1){

            getOpeningGui(player);
        }else if(args.length > 1){
            player.sendMessage(getTANString() + Lang.TOO_MANY_ARGS_ERROR.getTranslation());
            player.sendMessage(getTANString() + Lang.CORRECT_SYNTAX_INFO.getTranslation(getSyntax()));
        }

    }

    private void getOpeningGui(Player player) {
        GuiManager2.OpenMainMenu(player);
    }



}



