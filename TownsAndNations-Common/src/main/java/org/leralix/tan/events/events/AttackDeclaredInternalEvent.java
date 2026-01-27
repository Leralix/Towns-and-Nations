package org.leralix.tan.events.events;

import org.leralix.tan.events.InternalEvent;
import org.tan.api.events.AttackDeclaredEvent;
import org.tan.api.interfaces.TanTerritory;

public class AttackDeclaredInternalEvent extends InternalEvent implements AttackDeclaredEvent {

    private final TanTerritory attackedTerritory;
    private final TanTerritory attackingTerritory;

    public AttackDeclaredInternalEvent(TanTerritory attackedTerritory, TanTerritory attackingTerritory) {
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
