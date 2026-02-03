package org.leralix.tan.events.events;

import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.NationData;
import org.leralix.tan.events.InternalEvent;
import org.tan.api.events.NationEvent;
import org.tan.api.interfaces.territory.TanNation;
import org.tan.api.interfaces.TanPlayer;

public abstract class AbstractNationInternalEvent extends InternalEvent implements NationEvent {

    private final NationData nationData;
    private final ITanPlayer executor;

    protected AbstractNationInternalEvent(NationData nationData, ITanPlayer executor) {
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
