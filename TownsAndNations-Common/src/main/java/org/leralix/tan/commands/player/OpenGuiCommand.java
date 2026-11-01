package org.leralix.tan.commands.player;


import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.gui.user.MainMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.Collections;
import java.util.List;


public class OpenGuiCommand extends PlayerSubCommand {

    private final PlayerDataStorage playerDataStorage;

    public OpenGuiCommand(PlayerDataStorage playerDataStorage) {
        this.playerDataStorage = playerDataStorage;
    }

    @Override
    public String getName() {
        return "gui";
    }

    @Override
    public String getDescription() {
        return Lang.TOWN_GUI_COMMAND_DESC.getDefault();
    }

    public int getArguments() {
        return 2;
    }

    @Override
    public String getSyntax() {
        return "/tan gui";
    }

    @Override
    public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args) {
        return Collections.emptyList();
    }

    @Override
    public void perform(Player player, String[] args) {
        if (args.length == 1) {
            getOpeningGui(player);
        } else if (args.length > 1) {
            ITanPlayer tanPlayer = playerDataStorage.get(player);
            TanChatUtils.message(tanPlayer, Lang.TOO_MANY_ARGS_ERROR);
            TanChatUtils.message(tanPlayer, Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));
        }

    }

    private void getOpeningGui(Player player) {
        new MainMenu(player);
    }
}



