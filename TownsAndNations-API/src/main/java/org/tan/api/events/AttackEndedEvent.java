package org.tan.api.events;

import org.tan.api.interfaces.territory.TanTerritory;
import org.tan.api.interfaces.war.attack.TanAttackResults;

/**
 * This event will only fire if the attack end in a normal way. Cancelling an attack will not fire any events
 */
public interface AttackEndedEvent extends TanEvent {

    TanTerritory getDefenderTerritory();

    TanTerritory getAttackerTerritory();

    /**
     * @return numerical stats from the finished attack
     */
    TanAttackResults getResults();

}
