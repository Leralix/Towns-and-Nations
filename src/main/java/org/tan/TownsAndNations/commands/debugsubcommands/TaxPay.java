package org.tan.TownsAndNations.commands.debugsubcommands;

import org.bukkit.entity.Player;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.commands.SubCommand;
import org.tan.TownsAndNations.storage.ClaimedChunkStorage;
import org.tan.TownsAndNations.storage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.TownDataStorage;
import org.tan.TownsAndNations.utils.ChatUtils;

import java.util.List;

import static org.tan.TownsAndNations.Tasks.DailyTasks.TaxPayment;

public class TaxPay extends SubCommand {

    @Override
    public String getName() {
        return "taxPay";
    }

    @Override
    public String getDescription() {
        return "Pay taxes for all players in town";
    }

    @Override
    public int getArguments() {
        return 0;
    }

    @Override
    public String getSyntax() {
        return "/tandebug taxpay";
    }
    public List<String> getTabCompleteSuggestions(Player player, String[] args){
        return null;
    }
    @Override
    public void perform(Player player, String[] args) {
        TaxPayment();
    }
}

