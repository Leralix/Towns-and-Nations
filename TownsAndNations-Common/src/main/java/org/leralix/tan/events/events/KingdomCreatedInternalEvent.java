package org.leralix.tan.events.events;

import org.leralix.tan.api.internal.wrappers.KingdomDataWrapper;
import org.leralix.tan.api.internal.wrappers.TanPlayerWrapper;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.KingdomData;
import org.leralix.tan.events.InternalEvent;
import org.tan.api.events.KingdomCreatedEvent;
import org.tan.api.interfaces.TanKingdom;
import org.tan.api.interfaces.TanPlayer;

public class KingdomCreatedInternalEvent extends InternalEvent implements KingdomCreatedEvent {

    private final KingdomData kingdomData;
    private final ITanPlayer executor;

    public KingdomCreatedInternalEvent(KingdomData kingdomData, ITanPlayer executor) {
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
