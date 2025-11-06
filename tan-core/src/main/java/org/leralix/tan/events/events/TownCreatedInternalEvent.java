package org.leralix.tan.events.events;

import org.leralix.tan.api.internal.wrappers.TanPlayerWrapper;
import org.leralix.tan.api.internal.wrappers.TownDataWrapper;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.events.InternalEvent;
import org.tan.api.events.TownCreatedEvent;
import org.tan.api.interfaces.TanPlayer;
import org.tan.api.interfaces.TanTown;

public class TownCreatedInternalEvent extends InternalEvent implements TownCreatedEvent {

  private final TownData townData;
  private final ITanPlayer executor;

  public TownCreatedInternalEvent(TownData townData, ITanPlayer executor) {
    this.townData = townData;
    this.executor = executor;
  }

  @Override
  public TanTown getTown() {
    return TownDataWrapper.of(townData);
  }

  @Override
  public TanPlayer getExecutor() {
    return TanPlayerWrapper.of(executor);
  }
}
