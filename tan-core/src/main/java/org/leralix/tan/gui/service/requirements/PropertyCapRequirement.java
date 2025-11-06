package org.leralix.tan.gui.service.requirements;

import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;

public class PropertyCapRequirement extends IndividualRequirement {

  private final int maxAmount;
  private final TownData territoryData;

  public PropertyCapRequirement(TownData territoryData, int maxAmount) {
    this.territoryData = territoryData;
    this.maxAmount = maxAmount;
  }

  @Override
  public String getLine(LangType langType) {
    int nbProperties = territoryData.getProperties().size();

    if (isInvalid()) {
      return Lang.GUI_PROPERTY_CAP_FULL.get(
          langType, Integer.toString(nbProperties), Integer.toString(maxAmount));
    } else {
      return Lang.GUI_PROPERTY_CAP.get(
          langType, Integer.toString(nbProperties), Integer.toString(maxAmount));
    }
  }

  @Override
  public boolean isInvalid() {
    return territoryData.getProperties().size() > maxAmount;
  }
}
