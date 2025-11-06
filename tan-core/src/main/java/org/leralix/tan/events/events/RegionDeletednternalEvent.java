package org.leralix.tan.events.events;

import org.leralix.tan.api.internal.wrappers.RegionDataWrapper;
import org.leralix.tan.api.internal.wrappers.TanPlayerWrapper;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.events.InternalEvent;
import org.tan.api.events.RegionDeletedEvent;
import org.tan.api.interfaces.TanPlayer;
import org.tan.api.interfaces.TanRegion;

public class RegionDeletednternalEvent extends InternalEvent implements RegionDeletedEvent {

  private final RegionData region;
  private final ITanPlayer player;

  public RegionDeletednternalEvent(RegionData region, ITanPlayer player) {
    this.region = region;
    this.player = player;
  }

  @Override
  public TanRegion getRegion() {
    return RegionDataWrapper.of(region);
  }

  @Override
  public TanPlayer getExecutor() {
    return TanPlayerWrapper.of(player);
  }
}
