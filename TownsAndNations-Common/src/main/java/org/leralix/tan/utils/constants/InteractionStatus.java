package org.leralix.tan.utils.constants;

import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.data.territory.permission.GeneralChunkSetting;

public enum InteractionStatus {
    ALWAYS,
    WAR_ONLY,
    PLAYER_CHOICE_AND_WAR,
    PLAYER_CHOICE,
    NEVER;


    public boolean canGrief(Territory territoryData, GeneralChunkSetting action){
        return switch (this) {
            case ALWAYS -> true;
            case WAR_ONLY -> territoryData.attackInProgress();
            case PLAYER_CHOICE_AND_WAR -> territoryData.attackInProgress() || territoryData.getChunkSettings().getChunkSetting().getOrDefault(action, false);
            case PLAYER_CHOICE -> territoryData.getChunkSettings().getChunkSetting().getOrDefault(action, false);
            case NEVER -> false;
        };
    }

}
