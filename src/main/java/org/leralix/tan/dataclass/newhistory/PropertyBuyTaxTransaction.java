package org.leralix.tan.dataclass.newhistory;

import org.leralix.tan.dataclass.PropertyData;
import org.leralix.tan.utils.text.StringUtil;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.lang.Lang;

public class PropertyBuyTaxTransaction extends TransactionHistory {
    public PropertyBuyTaxTransaction(TownData town, PropertyData propertyData, double tax) {
        super(town.getID(), propertyData.getPropertyID(), tax);
    }

    public PropertyBuyTaxTransaction(String date, String territoryID, String propertyID, double amount) {
        super(date, territoryID, propertyID, amount);
    }

    @Override
    public TransactionHistoryEnum getType() {
        return TransactionHistoryEnum.PROPERTY_BUY_TAX;
    }

    @Override
    public String addLoreLine() {
        TownData territoryData = (TownData) getTerritoryData();
        PropertyData propertyData = territoryData.getProperty(getTransactionParty());
        return Lang.PROPERTY_SALE_LINE.get(territoryData.getName(), propertyData.getName(), StringUtil.getColoredMoney(-getAmount()));
    }
}
