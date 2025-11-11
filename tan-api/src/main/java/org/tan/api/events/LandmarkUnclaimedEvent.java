package org.tan.api.events;

import org.tan.api.interfaces.TanLandmark;
import org.tan.api.interfaces.TanTerritory;

public interface LandmarkUnclaimedEvent extends TanEvent {
  TanLandmark getLandmark();

  TanTerritory getOldOwner();
}
