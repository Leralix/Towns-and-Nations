package org.leralix.tan.dataclass.newhistory;

import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.utils.StringUtil;
import org.leralix.tan.lang.Lang;

public class MiscellaneousHistory extends TransactionHistory {

    public MiscellaneousHistory(String date, String territoryDataID, double amount) {
        super(date, territoryDataID, null, amount);
    }

    public MiscellaneousHistory(TerritoryData territoryData, double amount) {
        super(territoryData.getID(), null, amount);
    }

    @Override
    public TransactionHistoryEnum getType() {
        return TransactionHistoryEnum.MISCELLANEOUS;
    }

    @Override
    public String addLoreLine() {
        return Lang.MISCELLANEOUS_PAYMENT_HISTORY_LORE.get(StringUtil.getColoredMoney(-getAmount()));
    }
}
