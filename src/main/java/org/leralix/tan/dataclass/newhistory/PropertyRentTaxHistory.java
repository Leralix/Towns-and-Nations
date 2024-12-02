package org.leralix.tan.dataclass.newhistory;

import org.leralix.tan.dataclass.PropertyData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.StringUtil;

public class PropertyRentTaxHistory extends TransactionHistory {

    public PropertyRentTaxHistory(String date, String territoryID, String propertyID,double amount) {
        super(date, territoryID, propertyID, amount);
    }
    public PropertyRentTaxHistory(TerritoryData territoryData, PropertyData propertyData, double amount) {
        super(territoryData.getID(), propertyData.getPropertyID(), amount);
    }


    @Override
    public TransactionHistoryEnum getType() {
        return TransactionHistoryEnum.PROPERTY_RENT_TAX;
    }

        @Override
        public String addLoreLine() {
            return Lang.CHUNK_PAYMENT_HISTORY_LORE.get(StringUtil.getColoredMoney(-getAmount()));
        }
    }
