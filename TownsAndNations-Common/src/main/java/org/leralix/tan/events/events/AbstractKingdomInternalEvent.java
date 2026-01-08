package org.leralix.tan.events.events;

import org.leralix.tan.api.internal.wrappers.KingdomDataWrapper;
import org.leralix.tan.api.internal.wrappers.TanPlayerWrapper;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.KingdomData;
import org.leralix.tan.events.InternalEvent;
import org.tan.api.events.KingdomEvent;
import org.tan.api.interfaces.TanKingdom;
import org.tan.api.interfaces.TanPlayer;

public abstract class AbstractKingdomInternalEvent extends InternalEvent implements KingdomEvent {

    private final KingdomData kingdomData;
    private final ITanPlayer executor;

    protected AbstractKingdomInternalEvent(KingdomData kingdomData, ITanPlayer executor) {
        this.kingdomData = kingdomData;
        this.executor = executor;
    }

    @Override
    public TanKingdom getKingdom() {
        return KingdomDataWrapper.of(kingdomData);
    }

    @Override
    public TanPlayer getExecutor() {
        return TanPlayerWrapper.of(executor);
    }
}
