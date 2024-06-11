package org.tan.TownsAndNations.commands.debugsubcommands;

import org.bukkit.entity.Player;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.commands.SubCommand;
import org.tan.TownsAndNations.storage.DataStorage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;
import org.tan.TownsAndNations.utils.ChatUtils;

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
    public List<String> getTabCompleteSuggestions(Player player, String[] args){
        return null;
    }
    @Override
    public void perform(Player player, String[] args) {
        TownDataStorage.saveStats();
        PlayerDataStorage.saveOldStats();
        player.sendMessage(ChatUtils.getTANString() + Lang.COMMAND_GENERIC_SUCCESS.get());
    }
}