package org.leralix.tan.events.events;

import org.leralix.tan.api.internal.wrappers.LandmarkDataWrapper;
import org.leralix.tan.api.internal.wrappers.TerritoryDataWrapper;
import org.leralix.tan.dataclass.Landmark;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.events.InternalEvent;
import org.tan.api.events.LandmarkClaimedEvent;
import org.tan.api.interfaces.TanLandmark;
import org.tan.api.interfaces.TanTerritory;

public class LandmarkClaimedInternalEvent extends InternalEvent implements LandmarkClaimedEvent {

  private final TanLandmark claimedLandmark;
  private final TanTerritory newOwner;

  public LandmarkClaimedInternalEvent(Landmark landmark, TerritoryData newOwner) {
    this.claimedLandmark = LandmarkDataWrapper.of(landmark);
    this.newOwner = TerritoryDataWrapper.of(newOwner);
  }

  @Override
  public TanLandmark getLandmark() {
    return claimedLandmark;
  }

  @Override
  public TanTerritory getNewOwner() {
    return newOwner;
  }
}
