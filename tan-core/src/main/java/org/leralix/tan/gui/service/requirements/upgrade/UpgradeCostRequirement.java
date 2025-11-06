package org.leralix.tan.gui.service.requirements.upgrade;

import java.util.List;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.service.requirements.AmountForUpgradeRequirement;
import org.leralix.tan.gui.service.requirements.IndividualRequirement;
import org.leralix.tan.upgrade.Upgrade;

public class UpgradeCostRequirement extends UpgradeRequirement {

  private final List<Integer> costs;

  public UpgradeCostRequirement(List<Integer> costs) {
    this.costs = costs;
  }

  @Override
  public IndividualRequirement toIndividualRequirement(
      Upgrade upgrade, TerritoryData territoryData, Player player) {
    return new AmountForUpgradeRequirement(territoryData, upgrade, costs);
  }
}
