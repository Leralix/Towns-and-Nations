package org.leralix.tan.commands.admin;


import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.gui.admin.AdminMainMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.Collections;
import java.util.List;


public class OpenAdminGUI extends PlayerSubCommand {

    private final PlayerDataStorage playerDataStorage;

    public OpenAdminGUI(PlayerDataStorage playerDataStorage) {
        this.playerDataStorage = playerDataStorage;
    }

    @Override
    public String getName() {
        return "gui";
    }

    @Override
    public String getDescription() {
        return Lang.ADMIN_OPEN_GUI.getDefault();
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
            new AdminMainMenu(player);
        }else if(args.length > 1){
            ITanPlayer tanPlayer = playerDataStorage.get(player);
            TanChatUtils.message(tanPlayer, Lang.TOO_MANY_ARGS_ERROR);
            TanChatUtils.message(tanPlayer, Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));
        }

    }

}



