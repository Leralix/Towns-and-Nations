package org.leralix.tan.events.events;

import org.leralix.tan.events.InternalEvent;
import org.tan.api.events.TerritoryVassalAcceptedEvent;
import org.tan.api.interfaces.territory.TanTerritory;

public class TerritoryVassalAcceptedInternalEvent extends InternalEvent implements TerritoryVassalAcceptedEvent {

    private final TanTerritory territoryData;
    private final TanTerritory newOverlordData;

    public TerritoryVassalAcceptedInternalEvent(TanTerritory vassal, TanTerritory newOverlordData) {
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
