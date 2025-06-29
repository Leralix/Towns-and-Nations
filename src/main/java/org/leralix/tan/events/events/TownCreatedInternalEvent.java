package org.leralix.tan.events.events;

import org.leralix.tan.api.wrappers.TownDataWrapper;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.events.InternalEvent;
import org.tan.api.events.TownCreatedEvent;
import org.tan.api.interfaces.TanTown;

public class TownCreatedInternalEvent extends InternalEvent implements TownCreatedEvent {


    private final TownData townData;

    public TownCreatedInternalEvent(TownData townData) {
        this.townData = townData;
    }

    @Override
    public TanTown getTown() {
        return TownDataWrapper.of(townData);
    }
}
