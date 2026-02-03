package org.tan.api.events;

import org.tan.api.interfaces.territory.TanTerritory;

/**
 * @deprecated  use AttackEndedEvent
 */
@Deprecated(since = "0.17.0", forRemoval = true)
public interface AttackWonByAttackerEvent extends TanEvent {

    TanTerritory getDefenderTerritory();

    TanTerritory getAttackerTerritory();
}
