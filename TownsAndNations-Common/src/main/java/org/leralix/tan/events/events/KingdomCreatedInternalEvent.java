package org.leralix.tan.events.events;

import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.KingdomData;
import org.tan.api.events.KingdomCreatedEvent;

public class KingdomCreatedInternalEvent extends AbstractKingdomInternalEvent implements KingdomCreatedEvent {

    public KingdomCreatedInternalEvent(KingdomData kingdomData, ITanPlayer executor) {
        super(kingdomData, executor);
    }
}
