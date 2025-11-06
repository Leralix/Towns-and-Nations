package org.leralix.tan.events.events;

import org.leralix.tan.api.internal.wrappers.LandmarkDataWrapper;
import org.leralix.tan.api.internal.wrappers.TerritoryDataWrapper;
import org.leralix.tan.dataclass.Landmark;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.events.InternalEvent;
import org.tan.api.events.LandmarkUnclaimedEvent;
import org.tan.api.interfaces.TanLandmark;
import org.tan.api.interfaces.TanTerritory;

public class LandmarkUnclaimedInternalEvent extends InternalEvent
    implements LandmarkUnclaimedEvent {

  private final TanLandmark unclaimedLandmark;
  private final TanTerritory oldOwner;

  public LandmarkUnclaimedInternalEvent(Landmark landmark, TerritoryData oldOwner) {
    this.unclaimedLandmark = LandmarkDataWrapper.of(landmark);
    this.oldOwner = TerritoryDataWrapper.of(oldOwner);
  }

  @Override
  public TanLandmark getLandmark() {
    return unclaimedLandmark;
  }

  @Override
  public TanTerritory getOldOwner() {
    return oldOwner;
  }
}
