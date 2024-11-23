package org.leralix.tan.dataclass.newhistory;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.StringUtil;

import java.util.UUID;

public class PlayerTaxHistory extends TransactionHistory {


    public PlayerTaxHistory(String date, String territoryDataID, String playerID, double amount) {
        super(date, territoryDataID, playerID, amount);
    }
    public PlayerTaxHistory(TerritoryData territoryData, PlayerData playerData, double amount) {
        super(territoryData.getID(),playerData.getID(),amount);
    }


    @Override
    public TransactionHistoryEnum getType() {
        return TransactionHistoryEnum.PLAYER_TAX;
    }

    @Override
    public String addLoreLine() {
        OfflinePlayer player = Bukkit.getPlayer(UUID.fromString(getTransactionParty()));
        if(getAmount() > 0) {
            return Lang.TAX_PAYMENT_HISTORY_LORE.get(player.getName(), StringUtil.getColoredMoney(getAmount()));
        } else {
            return Lang.TAX_PAYMENT_HISTORY_LORE_NOT_ENOUGH_MONEY.get(player.getName());
        }
    }
}
