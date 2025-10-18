package org.leralix.tan.gui.service.requirements;

import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;

public class LevelRequirement extends IndividualRequirement {

    private final int requiredLevel;
    private final TerritoryData territoryData;

    public LevelRequirement(TerritoryData territoryData, int requiredLevel) {
        this.territoryData = territoryData;
        this.requiredLevel = requiredLevel;
    }

    @Override
    public String getLine(LangType langType) {
        if(isInvalid()){
            return Lang.REQUIREMENT_MAIN_LEVEL_NEGATIVE.get(langType, Integer.toString(requiredLevel));
        } else {
            return Lang.REQUIREMENT_MAIN_LEVEL_POSITIVE.get(langType, Integer.toString(requiredLevel));
        }
    }

    @Override
    public boolean isInvalid() {
        return territoryData.getNewLevel().getMainLevel() < requiredLevel;
    }
}
