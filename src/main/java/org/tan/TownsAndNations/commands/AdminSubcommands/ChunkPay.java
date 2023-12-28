package org.tan.TownsAndNations.commands.AdminSubcommands;

import org.bukkit.entity.Player;
import org.tan.TownsAndNations.commands.SubCommand;

import java.util.List;

import static org.tan.TownsAndNations.Tasks.DailyTasks.ChunkPayment;
import static org.tan.TownsAndNations.Tasks.DailyTasks.TaxPayment;

public class ChunkPay extends SubCommand {

    @Override
    public String getName() {
        return "chunkPay";
    }

    @Override
    public String getDescription() {
        return "Pay taxes for all chunks in towns";
    }

    @Override
    public int getArguments() {
        return 0;
    }

    @Override
    public String getSyntax() {
        return "/tandebug chunkPay";
    }
    public List<String> getTabCompleteSuggestions(Player player, String[] args){
        return null;
    }
    @Override
    public void perform(Player player, String[] args) {
        ChunkPayment();
    }
}

