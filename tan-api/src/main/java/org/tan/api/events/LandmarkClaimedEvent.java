package org.tan.api.events;

import org.tan.api.interfaces.TanLandmark;
import org.tan.api.interfaces.TanTerritory;

public interface LandmarkClaimedEvent extends TanEvent {
  TanLandmark getLandmark();

  TanTerritory getNewOwner();
}
