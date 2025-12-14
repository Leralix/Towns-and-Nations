package org.leralix.tan.events.events;

import org.leralix.tan.api.internal.wrappers.TerritoryDataWrapper;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.events.InternalEvent;
import org.tan.api.events.AttackEndedEvent;
import org.tan.api.interfaces.TanTerritory;
import org.tan.api.interfaces.war.TanAttackResults;

public class AttackEndedInternalEvent extends InternalEvent implements AttackEndedEvent {

    private final TerritoryData attackedTerritory;
    private final TerritoryData attackingTerritory;
    private final TanAttackResults attackResults;

    public AttackEndedInternalEvent(TerritoryData attackedTerritory, TerritoryData attackingTerritory, TanAttackResults attackResults) {
        super();
        this.attackedTerritory = attackedTerritory;
        this.attackingTerritory = attackingTerritory;
        this.attackResults = attackResults;
    }

    @Override
    public TanTerritory getDefenderTerritory() {
        return TerritoryDataWrapper.of(attackedTerritory);
    }

    @Override
    public TanTerritory getAttackerTerritory() {
        return TerritoryDataWrapper.of(attackingTerritory);
    }

    @Override
    public TanAttackResults getResults() {
        return attackResults;
    }
}
