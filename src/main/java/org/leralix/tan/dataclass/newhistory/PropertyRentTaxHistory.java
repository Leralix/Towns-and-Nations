package org.leralix.tan.dataclass.newhistory;

import org.leralix.tan.dataclass.PropertyData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.StringUtil;

public class PropertyRentTaxHistory extends TransactionHistory {

    public PropertyRentTaxHistory(String date, String territoryID, String propertyID, double amount) {
        super(date, territoryID, propertyID, amount);
    }


    @Override
    public TransactionHistoryEnum getType() {
        return TransactionHistoryEnum.PROPERTY_RENT_TAX;
    }

    @Override
    public String addLoreLine() {
        TownData territoryData = (TownData) getTerritoryData();
        PropertyData propertyData = territoryData.getProperty(getTransactionParty());
        return Lang.PROPERTY_RENT_LINE.get(territoryData.getName(), propertyData.getName(), StringUtil.getColoredMoney(-getAmount()));
    }

}
