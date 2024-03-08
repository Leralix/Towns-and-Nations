package org.tan.TownsAndNations.commands.debugsubcommands;

import org.bukkit.entity.Player;
import org.tan.TownsAndNations.commands.SubCommand;
import org.tan.TownsAndNations.utils.ArchiveUtil;

import java.util.List;

import static org.tan.TownsAndNations.Tasks.DailyTasks.*;

public class SkipDay extends SubCommand {

    @Override
    public String getName() {
        return "skipday";
    }

    @Override
    public String getDescription() {
        return "pay taxes, pay salaries, and collect chunk taxes for evey town";
    }

    @Override
    public int getArguments() {
        return 0;
    }

    @Override
    public String getSyntax() {
        return "/tandebug skipday";
    }
    public List<String> getTabCompleteSuggestions(Player player, String[] args){
        return null;
    }
    @Override
    public void perform(Player player, String[] args) {
        TownTaxPayment();
        RegionTaxPayment();
        ChunkPayment();
        SalaryPayment();
        ClearOldTaxes();
        ArchiveUtil.archiveFiles();
    }
}

