package org.leralix.tan.gui.service.requirements.upgrade;

import org.bukkit.entity.Player;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.data.upgrade.Upgrade;
import org.leralix.tan.gui.service.requirements.AmountForUpgradeRequirement;
import org.leralix.tan.gui.service.requirements.IndividualRequirement;

import java.util.List;

public class UpgradeCostRequirement extends UpgradeRequirement{

    private final List<Integer> costs;

    public UpgradeCostRequirement(List<Integer> costs) {
        this.costs = costs;
    }

    @Override
    public IndividualRequirement toIndividualRequirement(Upgrade upgrade, TerritoryData territoryData, Player player) {
        return new AmountForUpgradeRequirement(territoryData, upgrade, costs);
    }
}
