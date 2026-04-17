package org.leralix.tan.listeners.chat.events.treasury;

import org.leralix.tan.data.territory.Territory;

public class SetRentPropertyRate extends SetSpecificRate {

    public SetRentPropertyRate(Territory territoryData) {
        super(territoryData);
    }

    @Override
    void setRate(double percentage) {
        territoryData.setTaxOnRentingProperty(percentage);
    }
}
