package org.leralix.tan.war.legacy;

import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.permissions.GeneralChunkSetting;

public enum GriefAllowed {
    ALWAYS,
    WAR_ONLY,
    PLAYER_CHOICE_AND_WAR,
    PLAYER_CHOICE,
    NEVER;


    public boolean canGrief(TerritoryData territoryData, GeneralChunkSetting action){
        return switch (this) {
            case ALWAYS -> true;
            case WAR_ONLY -> territoryData.isAtWar();
            case PLAYER_CHOICE_AND_WAR -> territoryData.isAtWar() || territoryData.getChunkSettings().getSetting(action);
            case PLAYER_CHOICE -> territoryData.getChunkSettings().getSetting(action);
            case NEVER -> false;
        };
    }

}
