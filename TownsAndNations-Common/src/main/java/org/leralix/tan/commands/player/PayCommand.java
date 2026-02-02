package org.leralix.tan.commands.player;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.database.transactions.TransactionManager;
import org.leralix.tan.storage.database.transactions.instance.PaymentTransaction;
import org.leralix.tan.storage.stored.PlayerDataStorage;
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
        LangType langType = playerDataStorage.get(player).getLang();
        if (args.length < 3) {
            TanChatUtils.message(player, Lang.NOT_ENOUGH_ARGS_ERROR.get(langType));
            TanChatUtils.message(player, Lang.CORRECT_SYNTAX_INFO.get(langType, getSyntax()));
            return;
        } else if (args.length > 3) {
            TanChatUtils.message(player, Lang.TOO_MANY_ARGS_ERROR.get(langType));
            TanChatUtils.message(player, Lang.CORRECT_SYNTAX_INFO.get(langType, getSyntax()));
            return;
        }

        Player receiver = Bukkit.getServer().getPlayer(args[1]);
        if (receiver == null) {
            TanChatUtils.message(player, Lang.PLAYER_NOT_FOUND.get(langType));
            return;
        }
        if (receiver.getUniqueId().equals(player.getUniqueId())) {
            TanChatUtils.message(player, Lang.PAY_SELF_ERROR.get(langType));
            return;
        }

        Location senderLocation = player.getLocation();
        Location receiverLocation = receiver.getLocation();
        if (senderLocation.getWorld() != receiverLocation.getWorld()) {
            TanChatUtils.message(player, Lang.INTERACTION_TOO_FAR_ERROR.get(langType));
            return;
        }
        if (senderLocation.distance(receiverLocation) > maxPayDistance) {
            TanChatUtils.message(player, Lang.INTERACTION_TOO_FAR_ERROR.get(langType));
            return;
        }
        int amount;
        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            TanChatUtils.message(player, Lang.SYNTAX_ERROR_AMOUNT.get(langType));
            return;
        }
        if (amount < 1) {
            TanChatUtils.message(player, Lang.PAY_MINIMUM_REQUIRED.get(langType));
            return;
        }
        if (EconomyUtil.getBalance(player) < amount) {
            TanChatUtils.message(player, Lang.PLAYER_NOT_ENOUGH_MONEY_EXTENDED.get(
                    langType,
                    Double.toString(amount - EconomyUtil.getBalance(player))));
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
        TanChatUtils.message(player, Lang.PAY_CONFIRMED_SENDER.get(langType, Integer.toString(amount), receiver.getName()));
        TanChatUtils.message(receiver, Lang.PAY_CONFIRMED_RECEIVER.get(playerDataStorage.get(receiver).getLang(), Integer.toString(amount), player.getName()));
    }


}



