package org.tan.api.events;

import org.tan.api.interfaces.territory.TanTerritory;

public interface TerritoryVassalAcceptedEvent {

    TanTerritory getTerritory();

    TanTerritory getNewOverlord();

}
