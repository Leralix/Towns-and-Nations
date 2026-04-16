package org.leralix.tan.events.events;

import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.events.InternalEvent;
import org.tan.api.events.WarStartEvent;
import org.tan.api.interfaces.territory.TanTerritory;

public class WarStartInternalEvent extends InternalEvent implements WarStartEvent {

    private final Territory attackingTerritory;

    private final Territory defendingTerritory;


    public WarStartInternalEvent(Territory attackingTerritory, Territory attackedTerritory){
        this.attackingTerritory = attackingTerritory;
        this.defendingTerritory = attackedTerritory;
    }

    @Override
    public TanTerritory getAttacker() {
        return attackingTerritory;
    }

    @Override
    public TanTerritory getDefender() {
        return defendingTerritory;
    }
}
