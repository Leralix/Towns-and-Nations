package org.leralix.tan.events.events;

import org.leralix.tan.api.internal.wrappers.TerritoryDataWrapper;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.events.InternalEvent;
import org.tan.api.events.TerritoryVassalForcedEvent;
import org.tan.api.interfaces.TanTerritory;

public class TerritoryVassalForcedInternalEvent extends InternalEvent
    implements TerritoryVassalForcedEvent {

  private final TerritoryData territoryData;
  private final TerritoryData newOverlordData;

  public TerritoryVassalForcedInternalEvent(TerritoryData vassal, TerritoryData newOverlordData) {
    this.territoryData = vassal;
    this.newOverlordData = newOverlordData;
  }

  @Override
  public TanTerritory getTerritory() {
    return TerritoryDataWrapper.of(territoryData);
  }

  @Override
  public TanTerritory getNewOverlord() {
    return TerritoryDataWrapper.of(newOverlordData);
  }
}
