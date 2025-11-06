package org.leralix.tan.events.events;

import org.leralix.tan.api.internal.wrappers.RegionDataWrapper;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.events.InternalEvent;
import org.tan.api.events.RegionCreatedEvent;
import org.tan.api.interfaces.TanPlayer;
import org.tan.api.interfaces.TanRegion;

public class RegionCreatedInternalEvent extends InternalEvent implements RegionCreatedEvent {

  private final RegionData region;
  private final TanPlayer player;

  public RegionCreatedInternalEvent(RegionData region, TanPlayer player) {
    this.region = region;
    this.player = player;
  }

  @Override
  public TanRegion getRegion() {
    return RegionDataWrapper.of(region);
  }

  @Override
  public TanPlayer getExecutor() {
    return player;
  }
}
