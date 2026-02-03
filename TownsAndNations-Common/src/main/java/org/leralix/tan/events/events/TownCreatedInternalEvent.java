package org.leralix.tan.events.events;

import org.leralix.tan.events.InternalEvent;
import org.tan.api.events.TownCreatedEvent;
import org.tan.api.interfaces.TanPlayer;
import org.tan.api.interfaces.territory.TanTown;

public class TownCreatedInternalEvent extends InternalEvent implements TownCreatedEvent {


    private final TanTown townData;
    private final TanPlayer executor;

    public TownCreatedInternalEvent(TanTown townData, TanPlayer executor) {
        this.townData = townData;
        this.executor = executor;
    }

    @Override
    public TanTown getTown() {
        return townData;
    }

    @Override
    public TanPlayer getExecutor() {
        return executor;
    }
}
