package org.tan.api.events;

import org.tan.api.interfaces.territory.TanNation;
import org.tan.api.interfaces.TanPlayer;

public interface NationEvent extends TanEvent {

    TanNation getNation();

    TanPlayer getExecutor();
}
