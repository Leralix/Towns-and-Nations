package org.tan.api.events;

import org.tan.api.interfaces.TanTerritory;

public interface WarStartEvent {

    TanTerritory getAttacker();

    TanTerritory getDefender();

}
