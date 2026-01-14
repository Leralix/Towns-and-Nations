package org.leralix.tan.events.events;

import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.NationData;
import org.tan.api.events.NationDeletedEvent;

public class NationDeletedInternalEvent extends AbstractNationInternalEvent implements NationDeletedEvent {

    public NationDeletedInternalEvent(NationData nationData, ITanPlayer executor) {
        super(nationData, executor);
    }
}
