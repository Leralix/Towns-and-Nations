package org.leralix.tan.events.events;

import org.leralix.tan.events.InternalEvent;
import org.tan.api.events.AttackEndedEvent;
import org.tan.api.interfaces.territory.TanTerritory;
import org.tan.api.interfaces.war.attack.TanAttackResults;

public class AttackEndedInternalEvent extends InternalEvent implements AttackEndedEvent {

    private final TanTerritory attackedTerritory;
    private final TanTerritory attackingTerritory;
    private final TanAttackResults attackResults;

    public AttackEndedInternalEvent(TanTerritory attackedTerritory, TanTerritory attackingTerritory, TanAttackResults attackResults) {
        super();
        this.attackedTerritory = attackedTerritory;
        this.attackingTerritory = attackingTerritory;
        this.attackResults = attackResults;
    }

    @Override
    public TanTerritory getDefenderTerritory() {
        return attackedTerritory;
    }

    @Override
    public TanTerritory getAttackerTerritory() {
        return attackingTerritory;
    }

    @Override
    public TanAttackResults getResults() {
        return attackResults;
    }
}
