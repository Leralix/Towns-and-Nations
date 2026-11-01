package org.leralix.tan.gui.service.requirements;

import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.data.upgrade.Upgrade;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.Placeholder;

import java.util.List;

public class OtherUpgradeRequirement extends IndividualRequirement{

    private final Upgrade otherUpgrade;
    private final int requiredLevel;
    private final Territory territoryData;

    public OtherUpgradeRequirement(Upgrade otherUpgrade, int requiredLevel, Territory territoryData) {
        this.otherUpgrade = otherUpgrade;
        this.requiredLevel = requiredLevel;
        this.territoryData = territoryData;
    }

    @Override
    public Placeholder getLine() {
        int currentLevel = territoryData.getNewLevel().getLevel(otherUpgrade);
        List<Placeholder> placeholders = List.of(
                otherUpgrade.getName(),
                Lang.of(String.valueOf(currentLevel)),
                Lang.of(String.valueOf(requiredLevel))
        );
        if(isInvalid()){
            return Lang.REQUIREMENT_UPGRADE_LEVEL_NEGATIVE.get(placeholders);
        }
        else {
            return Lang.REQUIREMENT_UPGRADE_LEVEL_POSITIVE.get(placeholders);
        }
    }

    @Override
    public boolean isInvalid() {
        return territoryData.getNewLevel().getLevel(otherUpgrade) < requiredLevel;
    }
}
