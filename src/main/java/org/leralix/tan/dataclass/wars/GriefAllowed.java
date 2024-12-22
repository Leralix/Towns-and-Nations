package org.leralix.tan.dataclass.wars;

import org.leralix.tan.dataclass.territory.TerritoryData;

public enum GriefAllowed {
    ALWAYS,
    WAR_ONLY,
    NEVER;


    public boolean canGrief(TerritoryData territoryData){
        if(this == ALWAYS){
            return true;
        }
        if(this == WAR_ONLY){
            return territoryData.isAtWar();
        }
        return false;
    }

}
