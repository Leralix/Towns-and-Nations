package org.leralix.tan.gui.service.requirements.upgrade;

import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.service.requirements.IndividualRequirement;
import org.leralix.tan.upgrade.Upgrade;

public abstract class UpgradeRequirement {


    public abstract IndividualRequirement toIndividualRequirement(Upgrade upgrade, TerritoryData territoryData);

}
