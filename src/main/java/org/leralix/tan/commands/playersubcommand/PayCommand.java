package org.leralix.tan.commands.playersubcommand;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.commands.SubCommand;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.config.ConfigTag;
import org.leralix.tan.utils.config.ConfigUtil;
import org.leralix.tan.economy.EconomyUtil;

import java.util.List;

import static org.leralix.tan.utils.ChatUtils.getTANString;


public class PayCommand extends SubCommand  {
    @Override
    public String getName() {
        return "pay";
    }

    @Override
    public String getDescription() {
        return Lang.PAY_COMMAND_DESC.get();
    }

    @Override
    public String getSyntax() {
        return "/tan pay <player> <amount>";
    }

    public int getArguments(){
        return 3;
    }

    @Override
    public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args){
        return payPlayerSuggestion(args);
    }

    @Override
    public void perform(Player player, String[] args){
        if (args.length < 3){
            player.sendMessage(getTANString() + Lang.NOT_ENOUGH_ARGS_ERROR.get());
            player.sendMessage(getTANString() + Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));
            return;
        }
        else if(args.length > 3){
            player.sendMessage(getTANString() + Lang.TOO_MANY_ARGS_ERROR.get());
            player.sendMessage(getTANString() + Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));
            return;
        }

        Player receiver = Bukkit.getServer().getPlayer(args[1]);
        if(receiver == null){
            player.sendMessage(getTANString() + Lang.PLAYER_NOT_FOUND);
            return;
        }
        if(receiver.getUniqueId().equals(player.getUniqueId())){
            player.sendMessage(getTANString() + Lang.PAY_SELF_ERROR.get());
            return;
        }
        PlayerData receiverData = PlayerDataStorage.get(receiver);
        PlayerData senderData = PlayerDataStorage.get(player);
        if(receiverData.haveTown() && senderData.haveTown() && (receiverData.getTown().getRelationWith(senderData.getTown()) == TownRelation.EMBARGO ||
                receiverData.getTown().getRelationWith(senderData.getTown()) == TownRelation.WAR)){
            player.sendMessage(getTANString() + Lang.PLAYER_PAY_AT_EMBARGO_ERROR.get());
            return;
        }
        Location senderLocation = player.getLocation();
        Location receiverLocation = receiver.getLocation();
        if(senderLocation.getWorld() != receiverLocation.getWorld()){
            player.sendMessage(getTANString() + Lang.INTERACTION_TOO_FAR_ERROR.get());
            return;
        }
        if(senderLocation.distance(receiverLocation) > ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("maxPayDistance")){
            player.sendMessage(getTANString() + Lang.INTERACTION_TOO_FAR_ERROR.get());
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



}



