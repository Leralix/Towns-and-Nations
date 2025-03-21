package org.leralix.tan.commands.debugsubcommand;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.leralix.lib.commands.SubCommand;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.TanChatUtils;
import org.leralix.tan.lang.Lang;

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
    public List<String> getTabCompleteSuggestions(CommandSender commandSender, String lowerCase, String[] args){
        return Collections.emptyList();
    }
    @Override
    public void perform(CommandSender commandSender, String[] args) {
        TownDataStorage.saveStats();
        PlayerDataStorage.getInstance().saveStats();
        commandSender.sendMessage(TanChatUtils.getTANString() + Lang.COMMAND_GENERIC_SUCCESS.get());
    }
}