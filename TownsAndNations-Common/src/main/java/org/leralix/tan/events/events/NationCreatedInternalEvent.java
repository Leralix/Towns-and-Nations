package org.leralix.tan.events.events;

import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.Nation;
import org.tan.api.events.NationCreatedEvent;

public class NationCreatedInternalEvent extends AbstractNationInternalEvent implements NationCreatedEvent {

    public NationCreatedInternalEvent(Nation nationData, ITanPlayer executor) {
        super(nationData, executor);
    }
}
