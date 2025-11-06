package org.leralix.tan.api.internal.wrappers;

import org.leralix.tan.dataclass.territory.RegionData;
import org.tan.api.interfaces.TanRegion;
import org.tan.api.interfaces.TanTerritory;

public class RegionDataWrapper extends TerritoryDataWrapper implements TanRegion {

  private final RegionData regionData;

  private RegionDataWrapper(RegionData regionData) {
    super(regionData);
    this.regionData = regionData;
  }

  public static RegionDataWrapper of(RegionData regionData) {
    if (regionData == null) {
      return null;
    }
    return new RegionDataWrapper(regionData);
  }

  @Override
  public TanTerritory getCapital() {
    return TerritoryDataWrapper.of(regionData.getCapital());
  }
}
