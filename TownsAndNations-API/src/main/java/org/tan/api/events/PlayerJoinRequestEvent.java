package org.tan.api.events;

import org.tan.api.interfaces.TanPlayer;
import org.tan.api.interfaces.TanTown;

public interface PlayerJoinRequestEvent extends TanEvent {

    TanPlayer getPlayer();

    TanTown getTown();
}
