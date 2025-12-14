package org.leralix.tan.events;

import org.tan.api.events.TanEvent;

import java.time.Instant;

public abstract class InternalEvent implements TanEvent {

    private final Instant timestamp;

    public InternalEvent() {
        this.timestamp = Instant.now();
    }

    public Instant getTimestamp() {
        return timestamp;
    }


}
