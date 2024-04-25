package org.tan.TownsAndNations.commands.debugsubcommands;

import org.bukkit.entity.Player;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.commands.SubCommand;
import org.tan.TownsAndNations.utils.ArchiveUtil;

import java.util.List;

import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;

public class CreateBackup extends SubCommand {

    @Override
    public String getName() {
        return "createBackup";
    }

    @Override
    public String getDescription() {
        return Lang.DEBUG_CREATE_BACKUP.get();
    }

    @Override
    public int getArguments() {
        return 0;
    }

    @Override
    public String getSyntax() {
        return "/tandebug createBackup";
    }
    public List<String> getTabCompleteSuggestions(Player player, String[] args){

        return null;
    }
    @Override
    public void perform(Player player, String[] args) {

        ArchiveUtil.archiveFiles();
        player.sendMessage(getTANString() + Lang.COMMAND_GENERIC_SUCCESS.get());
    }

}
