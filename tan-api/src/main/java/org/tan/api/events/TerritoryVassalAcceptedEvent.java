package org.tan.api.events;

import org.tan.api.interfaces.TanTerritory;

public interface TerritoryVassalAcceptedEvent extends TanEvent {
  TanTerritory getTerritory();

  TanTerritory getNewOverlord();
}
