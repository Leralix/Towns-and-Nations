package org.leralix.tan.gui.service.requirements;

import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;

public class LeaderRequirement extends IndividualRequirement {

  private final TerritoryData territoryData;
  private final ITanPlayer tanPlayer;

  public LeaderRequirement(TerritoryData territoryData, ITanPlayer tanPlayer) {
    this.territoryData = territoryData;
    this.tanPlayer = tanPlayer;
  }

  @Override
  public String getLine(LangType langType) {
    if (isInvalid()) {
      return Lang.REQUIREMENT_TERRITORY_LEADER_NEGATIVE.get(langType);
    } else {
      return Lang.REQUIREMENT_TERRITORY_LEADER_POSITIVE.get(langType);
    }
  }

  @Override
  public boolean isInvalid() {
    return !territoryData.isLeader(tanPlayer);
  }
}
