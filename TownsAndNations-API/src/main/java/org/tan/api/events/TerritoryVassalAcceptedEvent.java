package org.tan.api.events;

import org.tan.api.interfaces.TanTerritory;

public interface TerritoryVassalAcceptedEvent {

    TanTerritory getTerritory();

    TanTerritory getNewOverlord();

}
