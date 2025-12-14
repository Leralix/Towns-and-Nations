package org.tan.api.events;

import org.tan.api.interfaces.TanTerritory;

public interface WarStartEvent extends TanEvent {

    TanTerritory getAttacker();

    TanTerritory getDefender();

}
