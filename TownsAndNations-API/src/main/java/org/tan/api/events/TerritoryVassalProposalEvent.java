package org.tan.api.events;

import org.tan.api.interfaces.territory.TanTerritory;

public interface TerritoryVassalProposalEvent extends TanEvent {

    TanTerritory getTerritory();

    TanTerritory getPotentialOverlord();

}
