package org.leralix.tan.listeners.chat.events.treasury;

import org.leralix.tan.data.territory.TerritoryData;

public class SetRentPropertyRate extends SetSpecificRate {

    public SetRentPropertyRate(TerritoryData territoryData) {
        super(territoryData);
    }

    @Override
    void setRate(double percentage) {
        territoryData.setTaxOnRentingProperty(percentage);
    }
}
