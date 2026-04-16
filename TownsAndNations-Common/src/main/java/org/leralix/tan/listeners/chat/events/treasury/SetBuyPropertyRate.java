package org.leralix.tan.listeners.chat.events.treasury;

import org.leralix.tan.data.territory.Territory;

public class SetBuyPropertyRate extends SetSpecificRate {

    public SetBuyPropertyRate(Territory territoryData){
        super(territoryData);
    }


    @Override
    void setRate(double percentage) {
        territoryData.setTaxOnBuyingProperty(percentage);
    }
}
