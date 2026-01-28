package org.leralix.tan.gui.service.requirements.upgrade;

import org.bukkit.entity.Player;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.data.upgrade.Upgrade;
import org.leralix.tan.gui.service.requirements.IndividualRequirement;
import org.leralix.tan.gui.service.requirements.LevelRequirement;

public class LevelUpgradeRequirement extends UpgradeRequirement{

    private final int requiredLevel;

    public LevelUpgradeRequirement(int requiredLevel) {
        this.requiredLevel = requiredLevel;
    }

    @Override
    public IndividualRequirement toIndividualRequirement(Upgrade upgrade, TerritoryData territoryData, Player player) {
        return new LevelRequirement(territoryData, requiredLevel);
    }
}
