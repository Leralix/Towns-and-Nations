package org.leralix.tan.data.territory.permission;

import org.leralix.tan.data.territory.Nation;
import org.leralix.tan.data.territory.Region;
import org.leralix.tan.data.territory.TerritoryData;

public enum PermissionGiven {

    TOWN,
    REGION,
    NATION,
    PROPERTY;

    public static PermissionGiven ofTerritory(TerritoryData territoryData) {
        if(territoryData instanceof Nation){
            return NATION;
        }
        if(territoryData instanceof Region){
            return REGION;
        }
        else {
            return TOWN;
        }
    }
}
