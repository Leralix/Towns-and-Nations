package org.leralix.tan.events.events;

import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.Nation;
import org.leralix.tan.events.InternalEvent;
import org.tan.api.events.NationEvent;
import org.tan.api.interfaces.TanPlayer;
import org.tan.api.interfaces.territory.TanNation;

public abstract class AbstractNationInternalEvent extends InternalEvent implements NationEvent {

    private final Nation nationData;
    private final ITanPlayer executor;

    protected AbstractNationInternalEvent(Nation nationData, ITanPlayer executor) {
        this.nationData = nationData;
        this.executor = executor;
    }

    @Override
    public TanNation getNation() {
        return nationData;
    }

    @Override
    public TanPlayer getExecutor() {
        return executor;
    }
}
