package org.tan.api.events;

import org.tan.api.interfaces.TanTerritory;

public interface TerritoryVassalProposalEvent extends TanEvent {

    TanTerritory getTerritory();

    TanTerritory getPotentialOverlord();

}
