package org.tan.TownsAndNations.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.commands.SubCommand;
import org.tan.TownsAndNations.utils.EconomyUtil;

import java.util.ArrayList;
import java.util.List;

import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;


public class PayCommand extends SubCommand  {
    @Override
    public String getName() {
        return "pay";
    }

    @Override
    public String getDescription() {
        return "give money to another player";
    }

    @Override
    public String getSyntax() {
        return "/tan pay <playerName> <amount>";
    }

    public int getArguments(){
        return 3;
    }

    @Override
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
    public void perform(Player player, String[] args){
        if(TownsAndNations.hasEconomy()){
            player.sendMessage(getTANString() + Lang.ECONOMY_EXISTS.get());
            return;
        }
        if (args.length < 3){
            player.sendMessage(getTANString() + Lang.NOT_ENOUGH_ARGS_ERROR.get());
            player.sendMessage(getTANString() + Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));
        }
        else if(args.length == 3){
            Player receiver = Bukkit.getServer().getPlayer(args[1]);

            if(receiver == null){
                player.sendMessage(getTANString() + Lang.PLAYER_NOT_FOUND);
                return;
            }

            if(receiver.getUniqueId().equals(player.getUniqueId())){
                player.sendMessage(getTANString() + Lang.PAY_SELF_ERROR.get());
                return;
            }

            int amount;

            try{
                amount = Integer.parseInt(args[2]);

            } catch (NumberFormatException e) {
                player.sendMessage(getTANString() + Lang.SYNTAX_ERROR_AMOUNT.get());
                return;
            }
            if(amount <1){
                player.sendMessage(getTANString() + Lang.PAY_MINIMUM_REQUIRED.get());
                return;
            }
            if(EconomyUtil.getBalance(player) < amount){
                player.sendMessage(getTANString() + Lang.PLAYER_NOT_ENOUGH_MONEY_EXTENDED.get(
                        amount - EconomyUtil.getBalance(player)));
                return;
            }

            EconomyUtil.removeFromBalance(player,amount);
            EconomyUtil.addFromBalance(receiver,amount);
            player.sendMessage(getTANString() + Lang.PAY_CONFIRMED_SENDER.get(amount,receiver.getName()));
            receiver.sendMessage(getTANString() + Lang.PAY_CONFIRMED_RECEIVER.get(amount,player.getName()));
        }
        else {
            player.sendMessage(getTANString() + Lang.TOO_MANY_ARGS_ERROR.get());
            player.sendMessage(getTANString() + Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));
        }
    }



}



