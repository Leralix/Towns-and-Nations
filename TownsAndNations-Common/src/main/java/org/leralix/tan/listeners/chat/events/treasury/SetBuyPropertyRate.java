package org.leralix.tan.listeners.chat.events.treasury;

import org.leralix.tan.data.territory.TerritoryData;

public class SetBuyPropertyRate extends SetSpecificRate {

    public SetBuyPropertyRate(TerritoryData territoryData){
        super(territoryData);
    }


    @Override
    void setRate(double percentage) {
        territoryData.setTaxOnBuyingProperty(percentage);
    }
}
