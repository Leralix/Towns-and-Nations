package org.leralix.tan.commands.debugsubcommand;

import org.bukkit.entity.Player;
import org.leralix.lib.commands.SubCommand;
import org.leralix.tan.utils.ArchiveUtil;
import org.leralix.tan.lang.Lang;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

public class SendReport extends SubCommand {

    @Override
    public String getName() {
        return "sendReport";
    }

    @Override
    public String getDescription() {
        return Lang.DEBUG_SEND_REPORT.get();
    }

    @Override
    public int getArguments() {
        return 0;
    }

    @Override
    public String getSyntax() {
        return "/tandebug report";
    }
    public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args){
        return Collections.emptyList();
    }
    @Override
    public void perform(Player player, String[] args) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();

        String reportName = "Report_of_" + player.getName() + "_at_" + dtf.format(now);
        ArchiveUtil.archiveFiles("reports", reportName);
        player.sendMessage(Lang.DEBUG_REPORT_CREATED.get());
    }
}

