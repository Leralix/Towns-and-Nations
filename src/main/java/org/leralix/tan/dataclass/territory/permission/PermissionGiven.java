package org.leralix.tan.dataclass.territory.permission;

import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TerritoryData;

public enum PermissionGiven {

    TOWN,
    REGION,
    PROPERTY;

    public static PermissionGiven ofTerritory(TerritoryData territoryData) {
        if(territoryData instanceof RegionData){
            return REGION;
        }
        else {
            return TOWN;
        }
    }
}
