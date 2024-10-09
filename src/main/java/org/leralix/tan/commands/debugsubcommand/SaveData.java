package org.leralix.tan.commands.debugsubcommand;

import org.bukkit.entity.Player;
import org.leralix.tan.Lang.Lang;
import org.leralix.tan.commands.SubCommand;
import org.leralix.tan.storage.DataStorage.PlayerDataStorage;
import org.leralix.tan.storage.DataStorage.TownDataStorage;
import org.leralix.tan.utils.ChatUtils;

import java.util.Collections;
import java.util.List;

public class SaveData extends SubCommand {

    @Override
    public String getName() {
        return "saveall";
    }

    @Override
    public String getDescription() {
        return Lang.DEBUG_SAVE_ALL_DATA.get();
    }

    @Override
    public int getArguments() {
        return 0;
    }

    @Override
    public String getSyntax() {
        return "/tandebug saveall";
    }
    public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args){
        return Collections.emptyList();
    }
    @Override
    public void perform(Player player, String[] args) {
        TownDataStorage.saveStats();
        PlayerDataStorage.saveStats();
        player.sendMessage(ChatUtils.getTANString() + Lang.COMMAND_GENERIC_SUCCESS.get());
    }
}