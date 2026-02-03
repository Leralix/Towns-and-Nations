package org.leralix.tan.events.events;

import org.leralix.tan.events.InternalEvent;
import org.tan.api.events.TerritoryVassalForcedEvent;
import org.tan.api.interfaces.territory.TanTerritory;

public class TerritoryVassalForcedInternalEvent extends InternalEvent implements TerritoryVassalForcedEvent {

    private final TanTerritory territoryData;
    private final TanTerritory newOverlordData;

    public TerritoryVassalForcedInternalEvent(TanTerritory vassal, TanTerritory newOverlordData) {
        this.territoryData = vassal;
        this.newOverlordData = newOverlordData;
    }


    @Override
    public TanTerritory getTerritory() {
        return territoryData;
    }

    @Override
    public TanTerritory getNewOverlord() {
        return newOverlordData;
    }
}
