package org.tan.api.events;

import org.tan.api.interfaces.TanPlayer;
import org.tan.api.interfaces.territory.TanRegion;

public interface RegionCreatedEvent extends TanEvent {

    TanRegion getRegion();

    TanPlayer getExecutor();
}
