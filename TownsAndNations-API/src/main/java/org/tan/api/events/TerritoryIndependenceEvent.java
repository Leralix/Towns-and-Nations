package org.tan.api.events;

import org.tan.api.interfaces.territory.TanTerritory;

public interface TerritoryIndependenceEvent extends TanEvent {

    TanTerritory getTerritory();

    TanTerritory getFormerOverlord();
}
