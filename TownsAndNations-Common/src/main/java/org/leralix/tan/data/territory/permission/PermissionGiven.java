package org.leralix.tan.data.territory.permission;

import org.leralix.tan.data.territory.NationData;
import org.leralix.tan.data.territory.RegionData;
import org.leralix.tan.data.territory.TerritoryData;

public enum PermissionGiven {

    TOWN,
    REGION,
    NATION,
    PROPERTY;

    public static PermissionGiven ofTerritory(TerritoryData territoryData) {
        if(territoryData instanceof NationData){
            return NATION;
        }
        if(territoryData instanceof RegionData){
            return REGION;
        }
        else {
            return TOWN;
        }
    }
}
