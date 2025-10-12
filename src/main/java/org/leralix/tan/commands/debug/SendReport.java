package org.leralix.tan.commands.debug;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.leralix.lib.commands.SubCommand;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.file.ArchiveUtil;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.Collections;
import java.util.List;

public class SendReport extends SubCommand {

    @Override
    public String getName() {
        return "report";
    }

    @Override
    public String getDescription() {
        return Lang.DEBUG_SEND_REPORT.getDefault();
    }

    @Override
    public int getArguments() {
        return 0;
    }

    @Override
    public String getSyntax() {
        return "/tandebug report";
    }
    public List<String> getTabCompleteSuggestions(CommandSender commandSender, String lowerCase, String[] args){
        return Collections.emptyList();
    }
    @Override
    public void perform(CommandSender commandSender, String[] args) {
        ArchiveUtil.sendReport(commandSender);
        TanChatUtils.message(commandSender, Lang.DEBUG_REPORT_CREATED);
    }
}

