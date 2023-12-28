package org.tan.TownsAndNations.commands.AdminSubcommands;

import org.bukkit.entity.Player;
import org.tan.TownsAndNations.commands.SubCommand;

import java.util.List;

import static org.tan.TownsAndNations.Tasks.DailyTasks.SalaryPayment;

public class SalaryPay extends SubCommand {

    @Override
    public String getName() {
        return "salarypay";
    }

    @Override
    public String getDescription() {
        return "Pay salaries for all players in towns";
    }

    @Override
    public int getArguments() {
        return 0;
    }

    @Override
    public String getSyntax() {
        return "/tanadmin salarypay";
    }
    public List<String> getTabCompleteSuggestions(Player player, String[] args){
        return null;
    }
    @Override
    public void perform(Player player, String[] args) {
        SalaryPayment();
    }
}

