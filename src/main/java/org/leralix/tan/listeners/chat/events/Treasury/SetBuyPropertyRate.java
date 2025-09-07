package org.leralix.tan.listeners.chat.events.Treasury;

import org.leralix.tan.dataclass.territory.TerritoryData;

public class SetBuyPropertyRate extends SetSpecificRate {

    public SetBuyPropertyRate(TerritoryData territoryData){
        super(territoryData);
    }


    @Override
    void setRate(double percentage) {
        territoryData.setTaxOnBuyingProperty(percentage);
    }
}
