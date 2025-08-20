package org.leralix.tan.dataclass.newhistory;

import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.text.StringUtil;

public class ChunkPaymentHistory extends TransactionHistory {

    public ChunkPaymentHistory(String date, String territoryID, double amount) {
        super(date, territoryID, null, amount);
    }

    public ChunkPaymentHistory(TerritoryData territoryData, double amount) {
        super(territoryData.getID(), null, amount);
    }


    @Override
    public TransactionHistoryEnum getType() {
        return TransactionHistoryEnum.CHUNK_SPENDING;
    }

    @Override
    public String addLoreLine() {
        return Lang.CHUNK_PAYMENT_HISTORY_LORE.get(StringUtil.getColoredMoney(-getAmount()));
    }
}
