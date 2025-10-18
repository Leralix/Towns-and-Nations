package org.leralix.tan.gui.service.requirements.upgrade;

import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.service.requirements.AmountForUpgradeRequirement;
import org.leralix.tan.gui.service.requirements.IndividualRequirement;
import org.leralix.tan.upgrade.Upgrade;

import java.util.List;

public class UpgradeCostRequirement extends UpgradeRequirement{

    private final List<Integer> costs;

    public UpgradeCostRequirement(List<Integer> costs) {
        this.costs = costs;
    }

    @Override
    public IndividualRequirement toIndividualRequirement(Upgrade upgrade, TerritoryData territoryData) {
        return new AmountForUpgradeRequirement(territoryData, upgrade, costs);
    }
}
