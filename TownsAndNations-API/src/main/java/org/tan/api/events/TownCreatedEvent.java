package org.tan.api.events;

import org.tan.api.interfaces.TanPlayer;
import org.tan.api.interfaces.TanTown;

public interface TownCreatedEvent extends TanEvent {

    TanTown getTown();

    TanPlayer getExecutor();
}
