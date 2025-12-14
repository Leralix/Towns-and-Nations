package org.tan.api.events;

import org.tan.api.interfaces.TanTerritory;

@Deprecated
public interface AttackWonByDefenderEvent extends TanEvent {

    TanTerritory getDefenderTerritory();

    TanTerritory getAttackerTerritory();
}
