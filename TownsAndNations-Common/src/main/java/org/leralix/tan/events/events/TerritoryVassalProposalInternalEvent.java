package org.leralix.tan.events.events;

import org.leralix.tan.events.InternalEvent;
import org.tan.api.events.TerritoryVassalProposalEvent;
import org.tan.api.interfaces.territory.TanTerritory;

public class TerritoryVassalProposalInternalEvent extends InternalEvent implements TerritoryVassalProposalEvent {

    private final TanTerritory territoryData;
    private final TanTerritory potentialOverlordData;

    public TerritoryVassalProposalInternalEvent(TanTerritory vassal, TanTerritory potentialOverlordData) {
        this.territoryData = vassal;
        this.potentialOverlordData = potentialOverlordData;
    }


    @Override
    public TanTerritory getTerritory() {
        return territoryData;
    }

    @Override
    public TanTerritory getPotentialOverlord() {
        return potentialOverlordData;
    }
}
