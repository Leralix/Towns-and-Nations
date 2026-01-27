package org.leralix.tan.events.events;

import org.leralix.tan.events.InternalEvent;
import org.tan.api.events.AttackWonByDefenderEvent;
import org.tan.api.interfaces.TanTerritory;

public class AttackWonByDefenderInternalEvent extends InternalEvent implements AttackWonByDefenderEvent {

    private final TanTerritory attackedTerritory;
    private final TanTerritory attackingTerritory;


    public AttackWonByDefenderInternalEvent(TanTerritory attackedTerritory, TanTerritory attackingTerritory) {
        super();
        this.attackedTerritory = attackedTerritory;
        this.attackingTerritory = attackingTerritory;
    }

    @Override
    public TanTerritory getDefenderTerritory() {
        return attackedTerritory;
    }

    @Override
    public TanTerritory getAttackerTerritory() {
        return attackingTerritory;
    }
}
