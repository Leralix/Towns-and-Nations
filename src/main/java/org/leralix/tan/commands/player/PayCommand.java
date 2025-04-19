package org.leralix.tan.commands.player;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.TanChatUtils;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.lang.Lang;

import java.util.List;


public class PayCommand extends PlayerSubCommand {
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
            player.sendMessage(TanChatUtils.getTANString() + Lang.NOT_ENOUGH_ARGS_ERROR.get());
            player.sendMessage(TanChatUtils.getTANString() + Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));
            return;
        }
        else if(args.length > 3){
            player.sendMessage(TanChatUtils.getTANString() + Lang.TOO_MANY_ARGS_ERROR.get());
            player.sendMessage(TanChatUtils.getTANString() + Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));
            return;
        }

        Player receiver = Bukkit.getServer().getPlayer(args[1]);
        if(receiver == null){
            player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NOT_FOUND);
            return;
        }
        if(receiver.getUniqueId().equals(player.getUniqueId())){
            player.sendMessage(TanChatUtils.getTANString() + Lang.PAY_SELF_ERROR.get());
            return;
        }
        PlayerData receiverData = PlayerDataStorage.getInstance().get(receiver);
        PlayerData senderData = PlayerDataStorage.getInstance().get(player);
        if(receiverData.hasTown() && senderData.hasTown() && !receiverData.getTown().canTradeWith(senderData.getTown())){
            player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_PAY_AT_EMBARGO_ERROR.get());
            return;
        }
        Location senderLocation = player.getLocation();
        Location receiverLocation = receiver.getLocation();
        if(senderLocation.getWorld() != receiverLocation.getWorld()){
            player.sendMessage(TanChatUtils.getTANString() + Lang.INTERACTION_TOO_FAR_ERROR.get());
            return;
        }
        if(senderLocation.distance(receiverLocation) > ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("maxPayDistance")){
            player.sendMessage(TanChatUtils.getTANString() + Lang.INTERACTION_TOO_FAR_ERROR.get());
            return;
        }
        int amount;
        try{
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            player.sendMessage(TanChatUtils.getTANString() + Lang.SYNTAX_ERROR_AMOUNT.get());
            return;
        }
        if(amount <1){
            player.sendMessage(TanChatUtils.getTANString() + Lang.PAY_MINIMUM_REQUIRED.get(EconomyUtil.getMoneyIcon()));
            return;
        }
        if(EconomyUtil.getBalance(player) < amount){
            player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NOT_ENOUGH_MONEY_EXTENDED.get(
                    amount - EconomyUtil.getBalance(player)));
            return;
        }
        EconomyUtil.removeFromBalance(player,amount);
        EconomyUtil.addFromBalance(receiver,amount);
        String moneyIcon = EconomyUtil.getMoneyIcon();
        player.sendMessage(TanChatUtils.getTANString() + Lang.PAY_CONFIRMED_SENDER.get(amount + moneyIcon,receiver.getName()));
        receiver.sendMessage(TanChatUtils.getTANString() + Lang.PAY_CONFIRMED_RECEIVER.get(amount + moneyIcon,player.getName()));
    }



}



