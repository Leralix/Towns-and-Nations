package org.leralix.tan.commands.player;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.database.transactions.TransactionManager;
import org.leralix.tan.storage.database.transactions.instance.PaymentTransaction;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.economy.EconomyUtil;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.List;


public class PayCommand extends PlayerSubCommand {

    private final double maxPayDistance;

    private final PlayerDataStorage playerDataStorage;

    public PayCommand(double maxPayDistance, PlayerDataStorage playerDataStorage) {
        this.maxPayDistance = maxPayDistance;
        this.playerDataStorage = playerDataStorage;
    }

    @Override
    public String getName() {
        return "pay";
    }

    @Override
    public String getDescription() {
        return Lang.PAY_COMMAND_DESC.getDefault();
    }

    @Override
    public String getSyntax() {
        return "/tan pay <player> <amount>";
    }

    public int getArguments() {
        return 3;
    }

    @Override
    public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args) {
        return payPlayerSuggestion(args);
    }

    @Override
    public void perform(Player player, String[] args) {
        ITanPlayer tanPlayer = playerDataStorage.get(player);
        if (args.length < 3) {
            TanChatUtils.message(tanPlayer, Lang.NOT_ENOUGH_ARGS_ERROR);
            TanChatUtils.message(tanPlayer, Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));
            return;
        } else if (args.length > 3) {
            TanChatUtils.message(tanPlayer, Lang.TOO_MANY_ARGS_ERROR);
            TanChatUtils.message(tanPlayer, Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));
            return;
        }

        Player receiver = Bukkit.getServer().getPlayer(args[1]);
        if (receiver == null) {
            TanChatUtils.message(tanPlayer, Lang.PLAYER_NOT_FOUND);
            return;
        }
        if (receiver.getUniqueId().equals(player.getUniqueId())) {
            TanChatUtils.message(tanPlayer, Lang.PAY_SELF_ERROR);
            return;
        }

        Location senderLocation = player.getLocation();
        Location receiverLocation = receiver.getLocation();
        if (senderLocation.getWorld() != receiverLocation.getWorld()) {
            TanChatUtils.message(tanPlayer, Lang.INTERACTION_TOO_FAR_ERROR);
            return;
        }
        if (senderLocation.distance(receiverLocation) > maxPayDistance) {
            TanChatUtils.message(tanPlayer, Lang.INTERACTION_TOO_FAR_ERROR);
            return;
        }
        int amount;
        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            TanChatUtils.message(tanPlayer, Lang.SYNTAX_ERROR_AMOUNT);
            return;
        }
        if (amount < 1) {
            TanChatUtils.message(tanPlayer, Lang.PAY_MINIMUM_REQUIRED);
            return;
        }
        if (EconomyUtil.getBalance(player) < amount) {
            TanChatUtils.message(tanPlayer, Lang.PLAYER_NOT_ENOUGH_MONEY_EXTENDED.get(
                    Double.toString(amount - EconomyUtil.getBalance(player)))
            );
            return;
        }
        EconomyUtil.removeFromBalance(player, amount);
        EconomyUtil.addFromBalance(receiver, amount);

        TransactionManager.getInstance().register(
                new PaymentTransaction(
                        player.getUniqueId().toString(),
                        receiver.getUniqueId().toString(),
                        amount
                )
        );
        TanChatUtils.message(tanPlayer, Lang.PAY_CONFIRMED_SENDER.get(Integer.toString(amount), receiver.getName()));
        ITanPlayer receiverData = playerDataStorage.get(receiver);
        TanChatUtils.message(receiverData, Lang.PAY_CONFIRMED_RECEIVER.get(Integer.toString(amount), player.getName()));
    }


}



