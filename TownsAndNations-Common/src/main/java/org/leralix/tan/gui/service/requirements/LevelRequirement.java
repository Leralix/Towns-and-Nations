package org.leralix.tan.gui.service.requirements;

import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.Placeholder;

public class LevelRequirement extends IndividualRequirement {

    private final int requiredLevel;
    private final Territory territoryData;

    public LevelRequirement(Territory territoryData, int requiredLevel) {
        this.territoryData = territoryData;
        this.requiredLevel = requiredLevel;
    }

    @Override
    public Placeholder getLine() {
        int currentLevel = territoryData.getNewLevel().getMainLevel();
        if(isInvalid()){
            return Lang.REQUIREMENT_MAIN_LEVEL_NEGATIVE.get(Integer.toString(currentLevel), Integer.toString(requiredLevel));
        } else {
            return Lang.REQUIREMENT_MAIN_LEVEL_POSITIVE.get(Integer.toString(currentLevel), Integer.toString(requiredLevel));
        }
    }

    @Override
    public boolean isInvalid() {
        return territoryData.getNewLevel().getMainLevel() < requiredLevel;
    }
}
