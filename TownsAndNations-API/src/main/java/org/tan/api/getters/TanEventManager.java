package org.tan.api.getters;

import org.tan.api.events.TanListener;

public interface TanEventManager {

    void registerEvents(TanListener listenerInstance);

}
