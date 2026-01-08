package org.leralix.tan.events.events;

import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.KingdomData;
import org.tan.api.events.KingdomDeletedEvent;

public class KingdomDeletedInternalEvent extends AbstractKingdomInternalEvent implements KingdomDeletedEvent {

    public KingdomDeletedInternalEvent(KingdomData kingdomData, ITanPlayer executor) {
        super(kingdomData, executor);
    }
}
