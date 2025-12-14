package org.leralix.tan.gui.service.requirements;

import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.upgrade.rewards.AggregatableStat;
import org.leralix.tan.upgrade.rewards.bool.BooleanStat;

public class BooleanStatRequirement<T extends BooleanStat & AggregatableStat<T>> extends IndividualRequirement {

    private final TerritoryData territoryData;
    private final Class<T> statClass;

    public BooleanStatRequirement(TerritoryData territoryData, Class<T> statClass) {
        super();
        this.territoryData = territoryData;
        this.statClass = statClass;
    }

    @Override
    public String getLine(LangType langType) {
        Lang statName = territoryData.getNewLevel().getStat(statClass).getStatName();
        if (isInvalid()) {
            return Lang.REQUIREMENT_UPGRADE_NEGATIVE.get(langType, statName.get(langType));
        } else {
            return Lang.REQUIREMENT_UPGRADE_POSITIVE.get(langType, statName.get(langType));
        }
    }

    @Override
    public boolean isInvalid() {
        return !territoryData.getNewLevel().getStat(statClass).isEnabled();
    }
}
