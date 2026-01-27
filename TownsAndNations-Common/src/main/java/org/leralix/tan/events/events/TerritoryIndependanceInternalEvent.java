package org.leralix.tan.events.events;

import org.leralix.tan.events.InternalEvent;
import org.tan.api.events.TerritoryIndependenceEvent;
import org.tan.api.interfaces.TanTerritory;

public class TerritoryIndependanceInternalEvent extends InternalEvent implements TerritoryIndependenceEvent {

    private final TanTerritory territory;
    private final TanTerritory formerOverlord;

    public TerritoryIndependanceInternalEvent(TanTerritory territory, TanTerritory formerOverlord) {
        this.territory = territory;
        this.formerOverlord = formerOverlord;
    }


    @Override
    public TanTerritory getTerritory() {
        return territory;
    }

    @Override
    public TanTerritory getFormerOverlord() {
        return formerOverlord;
    }
}
