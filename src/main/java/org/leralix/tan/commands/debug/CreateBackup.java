package org.leralix.tan.commands.debug;

import org.bukkit.command.CommandSender;
import org.leralix.lib.commands.SubCommand;
import org.leralix.tan.utils.ArchiveUtil;
import org.leralix.tan.utils.TanChatUtils;
import org.leralix.tan.lang.Lang;

import java.util.ArrayList;
import java.util.List;

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
    public List<String> getTabCompleteSuggestions(CommandSender commandSender, String lowerCase, String[] args){
        return new ArrayList<>();
    }
    @Override
    public void perform(CommandSender commandSender, String[] args) {
        ArchiveUtil.archiveFiles();
        commandSender.sendMessage(TanChatUtils.getTANString() + Lang.COMMAND_GENERIC_SUCCESS.get());
    }

}
