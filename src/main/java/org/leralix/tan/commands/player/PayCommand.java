package org.leralix.tan.commands.player;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.List;


public class PayCommand extends PlayerSubCommand {

    private final double maxPayDistance;

    public PayCommand(double maxPayDistance){
        this.maxPayDistance = maxPayDistance;
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
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getSync(player);
        LangType langType = tanPlayer.getLang();
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
        TanChatUtils.message(player, Lang.PAY_CONFIRMED_SENDER.get(langType, Integer.toString(amount), receiver.getName()));
        TanChatUtils.message(receiver, Lang.PAY_CONFIRMED_RECEIVER.get(receiver, Integer.toString(amount), player.getName()));
    }


}



