package org.leralix.tan.events.events;

import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.events.InternalEvent;
import org.tan.api.events.TownDeletedEvent;
import org.tan.api.interfaces.TanPlayer;
import org.tan.api.interfaces.TanTown;

public class TownDeletedInternalEvent extends InternalEvent implements TownDeletedEvent {


    private final TanTown townData;
    private final TanPlayer executor;

    public TownDeletedInternalEvent(TownData townData, TanPlayer executor) {
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
