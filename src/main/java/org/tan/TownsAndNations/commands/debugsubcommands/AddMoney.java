package org.tan.TownsAndNations.commands.debugsubcommands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.commands.SubCommand;
import org.tan.TownsAndNations.storage.PlayerDataStorage;

import java.util.ArrayList;
import java.util.List;

import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;

public class AddMoney extends SubCommand {

    @Override
    public String getName() {
        return "addmoney";
    }

    @Override
    public String getDescription() {
        return "Add money to a player.";
    }

    @Override
    public int getArguments() {
        return 2;
    }

    @Override
    public String getSyntax() {
        return "/tandebug addmoney <player> <amount>";
    }
    public List<String> getTabCompleteSuggestions(Player player, String[] args){
        List<String> suggestions = new ArrayList<>();
        if (args.length == 2) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                suggestions.add(p.getName());
            }
        }
        if (args.length == 3) {
            suggestions.add("<amount>");
        }
        return suggestions;
    }
    @Override
    public void perform(Player player, String[] args) {

        if (args.length < 2) {
            player.sendMessage(getTANString() + Lang.NOT_ENOUGH_ARGS_ERROR.getTranslation());
            player.sendMessage(getTANString() + Lang.CORRECT_SYNTAX_INFO.getTranslation(getSyntax()));
        }
        if (args.length == 3) {
            PlayerData target = PlayerDataStorage.get(Bukkit.getServer().getPlayer(args[1]));
            int amount;
            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                player.sendMessage(getTANString() + Lang.SYNTAX_ERROR_AMOUNT.getTranslation());
                return;
            }

            target.addToBalance(amount);
            player.sendMessage(getTANString() + Lang.ADD_MONEY_COMMAND_SUCCESS.getTranslation(amount,target.getName()));
        }
        else{
            player.sendMessage(getTANString() + Lang.TOO_MANY_ARGS_ERROR.getTranslation());
            player.sendMessage(getTANString() + Lang.CORRECT_SYNTAX_INFO.getTranslation(getSyntax()));
        }
    }
}