package org.tan.TownsAndNations.commands.subcommands;


import org.tan.TownsAndNations.GUI.GuiManager2;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.commands.SubCommand;


import org.bukkit.entity.Player;

import java.util.List;

import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;


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
    public List<String> getTabCompleteSuggestions(Player player, String[] args){
        return null;
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



