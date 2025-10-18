package org.leralix.tan.gui.service.requirements.upgrade;

import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.service.requirements.IndividualRequirement;
import org.leralix.tan.gui.service.requirements.LevelRequirement;
import org.leralix.tan.upgrade.Upgrade;

public class LevelUpgradeRequirement extends UpgradeRequirement{

    private final int requiredLevel;

    public LevelUpgradeRequirement(int requiredLevel) {
        this.requiredLevel = requiredLevel;
    }

    @Override
    public IndividualRequirement toIndividualRequirement(Upgrade upgrade, TerritoryData territoryData) {
        return new LevelRequirement(territoryData, requiredLevel);
    }
}
