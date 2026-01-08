package org.leralix.tan.dataclass.territory.permission;

import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.KingdomData;
import org.leralix.tan.dataclass.territory.TerritoryData;

public enum PermissionGiven {

    TOWN,
    REGION,
    KINGDOM,
    PROPERTY;

    public static PermissionGiven ofTerritory(TerritoryData territoryData) {
        if(territoryData instanceof KingdomData){
            return KINGDOM;
        }
        if(territoryData instanceof RegionData){
            return REGION;
        }
        else {
            return TOWN;
        }
    }
}
