package org.leralix.tan.gui.service.requirements.upgrade;

import org.bukkit.entity.Player;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.data.upgrade.Upgrade;
import org.leralix.tan.gui.service.requirements.IndividualRequirement;
import org.leralix.tan.gui.service.requirements.OtherUpgradeRequirement;
import org.leralix.tan.gui.service.requirements.UpgradeErrorRequirement;
import org.leralix.tan.utils.constants.Constants;

public class OtherUpgradeRequirementBuilder extends UpgradeRequirement {

    private final String otherUpgradeId;
    private final int requiredLevel;

    public OtherUpgradeRequirementBuilder(String otherUpgradeId, int requiredLevel) {
        this.otherUpgradeId = otherUpgradeId;
        this.requiredLevel = requiredLevel;
    }

    @Override
    public IndividualRequirement toIndividualRequirement(Upgrade upgrade, TerritoryData territoryData, Player player) {
        Upgrade otherUpgrade = Constants.getUpgradeStorage().getUpgrade(territoryData, otherUpgradeId);
        if(otherUpgrade == null){
            return new UpgradeErrorRequirement();
        }
        return new OtherUpgradeRequirement(otherUpgrade, requiredLevel, territoryData);
    }
}
