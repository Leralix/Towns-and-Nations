package org.leralix.tan.events.events;

import org.leralix.tan.api.internal.wrappers.TerritoryDataWrapper;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.events.InternalEvent;
import org.tan.api.events.TerritoryVassalProposalEvent;
import org.tan.api.interfaces.TanTerritory;

public class TerritoryVassalProposalInternalEvent extends InternalEvent
    implements TerritoryVassalProposalEvent {

  private final TerritoryData territoryData;
  private final TerritoryData potentialOverlordData;

  public TerritoryVassalProposalInternalEvent(
      TerritoryData vassal, TerritoryData potentialOverlordData) {
    this.territoryData = vassal;
    this.potentialOverlordData = potentialOverlordData;
  }

  @Override
  public TanTerritory getTerritory() {
    return TerritoryDataWrapper.of(territoryData);
  }

  @Override
  public TanTerritory getPotentialOverlord() {
    return TerritoryDataWrapper.of(potentialOverlordData);
  }
}
