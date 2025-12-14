package org.tan.api.events;

import org.tan.api.interfaces.TanTerritory;

@Deprecated
public interface AttackWonByAttackerEvent extends TanEvent {

    TanTerritory getDefenderTerritory();

    TanTerritory getAttackerTerritory();
}
