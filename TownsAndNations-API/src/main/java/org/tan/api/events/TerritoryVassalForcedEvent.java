package org.tan.api.events;

import org.tan.api.interfaces.territory.TanTerritory;

public interface TerritoryVassalForcedEvent extends TanEvent {

    TanTerritory getTerritory();

    TanTerritory getNewOverlord();

}
