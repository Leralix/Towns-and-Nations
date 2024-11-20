package org.leralix.tan.dataclass.newhistory;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.leralix.tan.dataclass.RankData;
import org.leralix.tan.dataclass.territory.ITerritoryData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.StringUtil;
import org.leralix.tan.utils.TerritoryUtil;

import java.util.UUID;

public class SalaryPaymentHistory extends TransactionHistory {


    public SalaryPaymentHistory(String date, String territoryDataID, String rankID, double amount) {
        super(date, territoryDataID, rankID, amount);
    }

    public SalaryPaymentHistory(ITerritoryData territoryData, String rankID, double amount) {
        super(territoryData.getID(), rankID, amount);
    }

    @Override
    public TransactionHistoryEnum getType() {
        return TransactionHistoryEnum.SALARY;
    }

    @Override
    public String addLoreLine() {
        ITerritoryData territoryData = TerritoryUtil.getTerritory(getTerritoryDataID());
        RankData rankData = territoryData.getRank(Integer.valueOf(getTransactionParty()));
        return Lang.SALARY_PAYMENT_HISTORY_LORE.get(rankData.getColoredName(), StringUtil.getColoredMoney(-getAmount()));
    }
}
