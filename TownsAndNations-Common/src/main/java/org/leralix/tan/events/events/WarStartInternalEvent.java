package org.leralix.tan.events.events;

import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.events.InternalEvent;
import org.tan.api.events.WarStartEvent;
import org.tan.api.interfaces.TanTerritory;

public class WarStartInternalEvent extends InternalEvent implements WarStartEvent {

    private final TerritoryData attackingTerritory;

    private final TerritoryData defendingTerritory;


    public WarStartInternalEvent(TerritoryData attackingTerritory, TerritoryData attackedTerritory){
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
