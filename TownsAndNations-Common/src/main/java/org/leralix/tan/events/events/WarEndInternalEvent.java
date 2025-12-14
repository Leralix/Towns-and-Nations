package org.leralix.tan.events.events;

import org.leralix.tan.api.internal.wrappers.TerritoryDataWrapper;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.events.InternalEvent;
import org.tan.api.events.WarEndEvent;
import org.tan.api.interfaces.TanTerritory;
import org.tan.api.interfaces.war.wargoals.TanWargoal;

import java.util.ArrayList;
import java.util.List;

public class WarEndInternalEvent extends InternalEvent implements WarEndEvent {


    private final TerritoryData winnerTerritory;

    private final TerritoryData loosingTerritory;

    private final List<TanWargoal> appliedWarGoals;


    public WarEndInternalEvent(TerritoryData winnerTerritory, TerritoryData loosingTerritory, List<? extends TanWargoal> appliedWarGoals){
        this.winnerTerritory = winnerTerritory;
        this.loosingTerritory = loosingTerritory;
        this.appliedWarGoals = new ArrayList<>(appliedWarGoals);
    }

    @Override
    public TanTerritory getWinner() {
        return TerritoryDataWrapper.of(winnerTerritory);
    }

    @Override
    public TanTerritory getDefeated() {
        return TerritoryDataWrapper.of(loosingTerritory);
    }

    @Override
    public List<TanWargoal> getAppliedWargoals() {
        return appliedWarGoals;
    }
}
