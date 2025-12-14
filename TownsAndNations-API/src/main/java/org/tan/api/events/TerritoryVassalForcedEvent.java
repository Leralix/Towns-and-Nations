package org.tan.api.events;

import org.tan.api.interfaces.TanTerritory;

public interface TerritoryVassalForcedEvent extends TanEvent {

    TanTerritory getTerritory();

    TanTerritory getNewOverlord();

}
