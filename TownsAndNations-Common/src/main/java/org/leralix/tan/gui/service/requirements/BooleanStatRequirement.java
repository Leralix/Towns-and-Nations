package org.leralix.tan.gui.service.requirements;

import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.data.upgrade.rewards.AggregatableStat;
import org.leralix.tan.data.upgrade.rewards.bool.BooleanStat;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.Placeholder;

public class BooleanStatRequirement<T extends BooleanStat & AggregatableStat<T>> extends IndividualRequirement {

    private final Territory territoryData;
    private final Class<T> statClass;

    public BooleanStatRequirement(Territory territoryData, Class<T> statClass) {
        super();
        this.territoryData = territoryData;
        this.statClass = statClass;
    }

    @Override
    public Placeholder getLine() {
        Lang statName = territoryData.getNewLevel().getStat(statClass).getStatName();
        if (isInvalid()) {
            return Lang.REQUIREMENT_UPGRADE_NEGATIVE.get(statName);
        } else {
            return Lang.REQUIREMENT_UPGRADE_POSITIVE.get(statName);
        }
    }

    @Override
    public boolean isInvalid() {
        return !territoryData.getNewLevel().getStat(statClass).isEnabled();
    }
}
