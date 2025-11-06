package org.leralix.tan.events;

import java.time.Instant;
import org.tan.api.events.TanEvent;

public abstract class InternalEvent implements TanEvent {

  private final Instant timestamp;

  public InternalEvent() {
    this.timestamp = Instant.now();
  }

  public Instant getTimestamp() {
    return timestamp;
  }
}
