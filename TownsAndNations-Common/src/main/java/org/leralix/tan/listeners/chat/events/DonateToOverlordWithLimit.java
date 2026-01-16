package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.listeners.chat.ChatListenerEvent;
import org.leralix.tan.storage.database.transactions.TransactionManager;
import org.leralix.tan.storage.database.transactions.instance.DonationTransaction;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TributePlayerDailyStorage;
import org.leralix.tan.storage.stored.TributeVassalDailyStorage;
import org.leralix.tan.utils.text.TanChatUtils;

public class DonateToOverlordWithLimit extends ChatListenerEvent {

    private final TerritoryData senderTerritory;
    private final TerritoryData overlordTerritory;

    public DonateToOverlordWithLimit(TerritoryData senderTerritory, TerritoryData overlordTerritory) {
        super();
        this.senderTerritory = senderTerritory;
        this.overlordTerritory = overlordTerritory;
    }

    @Override
    public boolean execute(Player player, String message) {
        Double amount = parseStringToDouble(message);
        if (amount == null) {
            TanChatUtils.message(player, Lang.SYNTAX_ERROR_AMOUNT.get(player));
            return false;
        }

        LangType langType = PlayerDataStorage.getInstance().get(player).getLang();
        double playerBalance = EconomyUtil.getBalance(player);

        if (playerBalance < amount) {
            TanChatUtils.message(player, Lang.PLAYER_NOT_ENOUGH_MONEY.get(langType));
            return false;
        }
        if (amount <= 0) {
            TanChatUtils.message(player, Lang.PAY_MINIMUM_REQUIRED.get(langType));
            return false;
        }

        var config = ConfigUtil.getCustomConfig(ConfigTag.MAIN);
        boolean enabled = config.getBoolean("tributeLimits.enabled", false);
        double perPlayerDailyLimit = config.getDouble("tributeLimits.perPlayerDailyLimit", 50000.0);
        double perVassalDailyLimit = config.getDouble("tributeLimits.perVassalDailyLimit", 400000.0);

        if (enabled) {
            String playerId = player.getUniqueId().toString();
            double playerDailyTotal = TributePlayerDailyStorage.getInstance().getAmount(playerId);
            if (playerDailyTotal + amount > perPlayerDailyLimit) {
                TanChatUtils.message(player, Lang.VALUE_EXCEED_MAXIMUM_ERROR.get(player, Integer.toString((int) perPlayerDailyLimit)));
                return false;
            }

            if (senderTerritory instanceof TownData) {
                String vassalId = senderTerritory.getID();
                double vassalDailyTotal = TributeVassalDailyStorage.getInstance().getAmount(vassalId);
                if (vassalDailyTotal + amount > perVassalDailyLimit) {
                    TanChatUtils.message(player, Lang.VALUE_EXCEED_MAXIMUM_ERROR.get(player, Integer.toString((int) perVassalDailyLimit)));
                    return false;
                }
            }
        }

        EconomyUtil.removeFromBalance(player, amount);
        overlordTerritory.addToBalance(amount);
        TransactionManager.getInstance().register(new DonationTransaction(overlordTerritory, player, amount));
        TanChatUtils.message(player, Lang.PLAYER_SEND_MONEY_SUCCESS.get(langType, Double.toString(amount), overlordTerritory.getBaseColoredName()), SoundEnum.MINOR_GOOD);

        if (enabled) {
            TributePlayerDailyStorage.getInstance().addAmount(player.getUniqueId().toString(), amount);
            if (senderTerritory instanceof TownData) {
                TributeVassalDailyStorage.getInstance().addAmount(senderTerritory.getID(), amount);
            }
        }

        return true;
    }
}
