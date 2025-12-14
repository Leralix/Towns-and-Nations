package org.leralix.tan.war.legacy;

import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.permissions.GeneralChunkSetting;

public enum InteractionStatus {
    ALWAYS,
    WAR_ONLY,
    PLAYER_CHOICE_AND_WAR,
    PLAYER_CHOICE,
    NEVER;


    public boolean canGrief(TerritoryData territoryData, GeneralChunkSetting action){
        return switch (this) {
            case ALWAYS -> true;
            case WAR_ONLY -> territoryData.isAtWar();
            case PLAYER_CHOICE_AND_WAR -> territoryData.isAtWar() || territoryData.getChunkSettings().getChunkSetting().get(action);
            case PLAYER_CHOICE -> territoryData.getChunkSettings().getChunkSetting().get(action);
            case NEVER -> false;
        };
    }

}
