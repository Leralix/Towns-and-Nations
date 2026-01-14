package org.leralix.tan.events.events;

import org.leralix.tan.api.internal.wrappers.NationDataWrapper;
import org.leralix.tan.api.internal.wrappers.TanPlayerWrapper;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.NationData;
import org.leralix.tan.events.InternalEvent;
import org.tan.api.events.NationEvent;
import org.tan.api.interfaces.TanNation;
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
        return NationDataWrapper.of(nationData);
    }

    @Override
    public TanPlayer getExecutor() {
        return TanPlayerWrapper.of(executor);
    }
}
