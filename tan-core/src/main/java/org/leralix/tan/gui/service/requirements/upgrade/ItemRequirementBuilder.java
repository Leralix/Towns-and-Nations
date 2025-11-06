package org.leralix.tan.gui.service.requirements.upgrade;

import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.service.requirements.IndividualRequirement;
import org.leralix.tan.gui.service.requirements.RessourceRequirement;
import org.leralix.tan.gui.service.requirements.model.ItemScope;
import org.leralix.tan.upgrade.Upgrade;

public class ItemRequirementBuilder extends UpgradeRequirement {

  private final ItemScope itemScope;
  private final int amount;

  public ItemRequirementBuilder(ItemScope itemScope, int amount) {
    this.itemScope = itemScope;
    this.amount = amount;
  }

  @Override
  public IndividualRequirement toIndividualRequirement(
      Upgrade upgrade, TerritoryData territoryData, Player player) {
    return new RessourceRequirement(itemScope, amount, player);
  }
}
