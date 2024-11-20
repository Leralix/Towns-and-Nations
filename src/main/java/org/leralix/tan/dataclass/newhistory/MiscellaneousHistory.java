package org.leralix.tan.dataclass.newhistory;

import org.leralix.tan.dataclass.territory.ITerritoryData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.StringUtil;

public class MiscellaneousHistory extends TransactionHistory {

    public MiscellaneousHistory(String date, String territoryDataID, double amount) {
        super(date, territoryDataID, null, amount);
    }

    public MiscellaneousHistory(ITerritoryData territoryData, double amount) {
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
