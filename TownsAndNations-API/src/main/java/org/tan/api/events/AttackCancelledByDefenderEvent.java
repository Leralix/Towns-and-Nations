package org.tan.api.events;

import org.tan.api.interfaces.TanTerritory;

public interface AttackCancelledByDefenderEvent extends TanEvent {

    TanTerritory getDefenderTerritory();

    TanTerritory getAttackerTerritory();
}
