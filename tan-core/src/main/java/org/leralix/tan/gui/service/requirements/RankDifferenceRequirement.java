package org.leralix.tan.gui.service.requirements;

import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.RankData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;

public class RankDifferenceRequirement extends IndividualRequirement {

  private final TerritoryData territoryData;
  private final ITanPlayer tanPlayer;
  private final RankData rankToCompare;

  public RankDifferenceRequirement(
      TerritoryData territoryData, ITanPlayer tanPlayer, RankData rankToCompare) {
    super();
    this.territoryData = territoryData;
    this.tanPlayer = tanPlayer;
    this.rankToCompare = rankToCompare;
  }

  @Override
  public String getLine(LangType langType) {
    return isInvalid()
        ? Lang.REQUIREMENT_RANK_LEVEL_NEGATIVE.get(
            langType, territoryData.getRank(tanPlayer).getName(), rankToCompare.getName())
        : Lang.REQUIREMENT_RANK_LEVEL_POSITIVE.get(
            langType, territoryData.getRank(tanPlayer).getName(), rankToCompare.getName());
  }

  @Override
  public boolean isInvalid() {
    if (territoryData.isLeader(tanPlayer)) {
      return false;
    }
    return territoryData.getRank(tanPlayer).getLevel() <= rankToCompare.getLevel();
  }
}
