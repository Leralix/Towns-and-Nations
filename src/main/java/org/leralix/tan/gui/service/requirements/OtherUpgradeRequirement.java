package org.leralix.tan.gui.service.requirements;

import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.upgrade.Upgrade;

public class OtherUpgradeRequirement extends IndividualRequirement{

    private final Upgrade otherUpgrade;
    private final int requiredLevel;
    private final TerritoryData territoryData;

    public OtherUpgradeRequirement(Upgrade otherUpgrade, int requiredLevel, TerritoryData territoryData) {
        this.otherUpgrade = otherUpgrade;
        this.requiredLevel = requiredLevel;
        this.territoryData = territoryData;
    }

    @Override
    public String getLine(LangType langType) {
        int currentLevel = territoryData.getNewLevel().getLevel(otherUpgrade);
        if(isInvalid()){
            return Lang.REQUIREMENT_UPGRADE_LEVEL_NEGATIVE.get(langType, otherUpgrade.getName(langType), String.valueOf(currentLevel), String.valueOf(requiredLevel));
        }
        else {
            return Lang.REQUIREMENT_UPGRADE_LEVEL_POSITIVE.get(langType, otherUpgrade.getName(langType), String.valueOf(currentLevel), String.valueOf(requiredLevel));
        }
    }

    @Override
    public boolean isInvalid() {
        return territoryData.getNewLevel().getLevel(otherUpgrade) < requiredLevel;
    }
}
