package org.leralix.tan.dataclass.newhistory;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.text.StringUtil;

import java.util.UUID;

public class PlayerDonationHistory extends TransactionHistory {


    public PlayerDonationHistory(String date, String territoryDataID, String playerID, double amount) {
        super(date, territoryDataID, playerID, amount);
    }

    public PlayerDonationHistory(TerritoryData territoryData, Player player, double amount) {
        super(territoryData.getID(), player.getUniqueId().toString(), amount);
    }


    @Override
    public TransactionHistoryEnum getType() {
        return TransactionHistoryEnum.DONATION;
    }

    @Override
    public String addLoreLine() {
        OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(getTransactionParty()));
        return Lang.DONATION_PAYMENT_HISTORY_LORE.get(player.getName(), StringUtil.getColoredMoney(getAmount()));
    }
}
