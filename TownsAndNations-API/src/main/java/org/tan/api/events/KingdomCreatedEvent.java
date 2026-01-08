package org.tan.api.events;

import org.tan.api.interfaces.TanKingdom;
import org.tan.api.interfaces.TanPlayer;

public interface KingdomCreatedEvent extends TanEvent {

    TanKingdom getKingdom();

    TanPlayer getExecutor();
}
