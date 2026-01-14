package org.leralix.tan.events.events;

import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.NationData;
import org.tan.api.events.NationCreatedEvent;

public class NationCreatedInternalEvent extends AbstractNationInternalEvent implements NationCreatedEvent {

    public NationCreatedInternalEvent(NationData nationData, ITanPlayer executor) {
        super(nationData, executor);
    }
}
