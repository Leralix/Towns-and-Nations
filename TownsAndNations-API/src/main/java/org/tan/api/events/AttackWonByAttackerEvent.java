package org.tan.api.events;

import org.tan.api.interfaces.TanTerritory;

public interface AttackWonByAttackerEvent extends TanEvent {

    TanTerritory getDefenderTerritory();

    TanTerritory getAttackerTerritory();
}
