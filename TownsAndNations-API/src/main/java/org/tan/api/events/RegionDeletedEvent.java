package org.tan.api.events;

import org.tan.api.interfaces.TanPlayer;
import org.tan.api.interfaces.TanRegion;

public interface RegionDeletedEvent extends TanEvent {

    TanRegion getRegion();

    TanPlayer getExecutor();

}
