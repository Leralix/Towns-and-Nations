package org.leralix.tan.commands.player;


import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.tan.gui.user.MainMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.PlayerDataStorage;

import java.util.Collections;
import java.util.List;


public class OpenGuiCommand extends PlayerSubCommand {
    @Override
    public String getName() {
        return "gui";
    }


    @Override
    public String getDescription() {
        return Lang.TOWN_GUI_COMMAND_DESC.getDefault();
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
            LangType lang = PlayerDataStorage.getInstance().get(player).getLang();
            player.sendMessage(Lang.TOO_MANY_ARGS_ERROR.get(lang));
            player.sendMessage(Lang.CORRECT_SYNTAX_INFO.get(lang, getSyntax()));
        }

    }

    private void getOpeningGui(Player player) {
        new MainMenu(player);
    }


}



