package org.tan.api.events;

import org.tan.api.interfaces.territory.TanTerritory;

public interface WarStartEvent extends TanEvent {

    TanTerritory getAttacker();

    TanTerritory getDefender();

}
