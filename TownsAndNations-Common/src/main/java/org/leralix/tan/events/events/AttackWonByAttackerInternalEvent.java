package org.leralix.tan.events.events;

import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.events.InternalEvent;
import org.tan.api.events.AttackWonByAttackerEvent;
import org.tan.api.interfaces.territory.TanTerritory;

public class AttackWonByAttackerInternalEvent extends InternalEvent implements AttackWonByAttackerEvent {

    private final TerritoryData attackedTerritory;
    private final TerritoryData attackingTerritory;


    public AttackWonByAttackerInternalEvent(TerritoryData attackedTerritory, TerritoryData attackingTerritory) {
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
