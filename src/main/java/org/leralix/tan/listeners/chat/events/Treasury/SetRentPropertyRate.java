package org.leralix.tan.listeners.chat.events.Treasury;

import org.leralix.tan.dataclass.territory.TerritoryData;

public class SetRentPropertyRate extends SetSpecificRate {

    public SetRentPropertyRate(TerritoryData territoryData) {
        super(territoryData);
    }

    @Override
    void setRate(double percentage) {
        territoryData.setTaxOnRentingProperty(percentage);
    }
}
