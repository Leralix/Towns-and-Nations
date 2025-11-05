package org.leralix.tan.events.events;

import org.leralix.tan.api.internal.wrappers.TerritoryDataWrapper;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.events.InternalEvent;
import org.tan.api.events.AttackCancelledByDefenderEvent;
import org.tan.api.interfaces.TanTerritory;

public class DefenderAcceptDemandsBeforeWarInternalEvent extends InternalEvent implements AttackCancelledByDefenderEvent {

    private final TerritoryData defendingTerritory;
    private final TerritoryData attackedTerritory;

    public DefenderAcceptDemandsBeforeWarInternalEvent(TerritoryData defendingTerritory, TerritoryData attackedTerritory) {
        super();
        this.defendingTerritory = defendingTerritory;
        this.attackedTerritory = attackedTerritory;
    }

    @Override
    public TanTerritory getDefenderTerritory() {
        return TerritoryDataWrapper.of(defendingTerritory);
    }

    @Override
    public TanTerritory getAttackerTerritory() {
        return TerritoryDataWrapper.of(attackedTerritory);
    }
}
