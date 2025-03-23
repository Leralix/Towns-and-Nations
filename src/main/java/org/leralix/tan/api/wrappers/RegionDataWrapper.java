package org.leralix.tan.api.wrappers;

import org.leralix.tan.dataclass.territory.RegionData;
import org.tan.api.interfaces.TanRegion;

public class RegionDataWrapper extends TerritoryDataWrapper implements TanRegion {

    private final RegionData regionData;

    private RegionDataWrapper(RegionData regionData){
        super(regionData);
        this.regionData = regionData;
    }

    public RegionDataWrapper of(RegionData regionData){
        if(regionData == null){
            return null;
        }
        return new RegionDataWrapper(regionData);
    }
}
