package org.leralix.tan.events.events;

import org.leralix.tan.api.wrappers.TerritoryDataWrapper;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.events.InternalEvent;
import org.tan.api.events.TerritoryIndependenceEvent;
import org.tan.api.interfaces.TanTerritory;

public class TerritoryIndependanceInternalEvent extends InternalEvent implements TerritoryIndependenceEvent {

    private final TerritoryData territory;
    private final TerritoryData formerOverlord;

    public TerritoryIndependanceInternalEvent(TerritoryData territory, TerritoryData formerOverlord) {
        this.territory = territory;
        this.formerOverlord = formerOverlord;
    }


    @Override
    public TanTerritory getTerritory() {
        return TerritoryDataWrapper.of(territory);
    }

    @Override
    public TanTerritory getFormerOverlord() {
        return TerritoryDataWrapper.of(formerOverlord);
    }
}
