package org.leralix.tan.gui.service.requirements.upgrade;

import org.bukkit.entity.Player;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.data.upgrade.Upgrade;
import org.leralix.tan.gui.service.requirements.IndividualRequirement;

public abstract class UpgradeRequirement {


    public abstract IndividualRequirement toIndividualRequirement(Upgrade upgrade, TerritoryData territoryData, Player player);

}
