package org.leralix.tan.commands.debug;

import org.bukkit.command.CommandSender;
import org.leralix.lib.commands.SubCommand;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.*;
import org.leralix.tan.utils.TanChatUtils;

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
        RegionDataStorage.getInstance().saveStats();
        TownDataStorage.getInstance().saveStats();
        PlayerDataStorage.getInstance().saveStats();
        NewClaimedChunkStorage.getInstance().save();
        LandmarkStorage.getInstance().save();
        CurrentWarStorage.save();
        commandSender.sendMessage(TanChatUtils.getTANString() + Lang.COMMAND_GENERIC_SUCCESS.get());
    }
}