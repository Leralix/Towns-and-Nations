package org.leralix.tan.events.events;

import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.NationData;
import org.tan.api.events.NationDeletedEvent;

public class NationDeletedInternalEvent extends AbstractNationInternalEvent implements NationDeletedEvent {

    public NationDeletedInternalEvent(NationData nationData, ITanPlayer executor) {
        super(nationData, executor);
    }
}
