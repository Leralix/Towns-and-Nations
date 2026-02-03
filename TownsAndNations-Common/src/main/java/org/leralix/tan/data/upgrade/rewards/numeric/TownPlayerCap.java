package org.leralix.tan.data.upgrade.rewards.numeric;

import org.leralix.tan.data.upgrade.rewards.AggregatableStat;
import org.leralix.tan.lang.Lang;

import java.util.List;

public class TownPlayerCap extends NumericStat implements AggregatableStat<TownPlayerCap> {

    /**
     * Default constructor
     * Needed to create an empty stat. Do not remove
     */
    @SuppressWarnings("unused")
    public TownPlayerCap() {
        super(0, false);
    }

    public TownPlayerCap(int maxAmount, boolean isUnlimited) {
       super(maxAmount, isUnlimited);
    }

    @Override
    public TownPlayerCap scale(int factor) {
        return new TownPlayerCap(maxAmount * factor, isUnlimited);
    }

    @Override
    public TownPlayerCap aggregate(List<TownPlayerCap> stats) {
        int totalCap = 0;
        boolean unlimitedFound = false;
        for (TownPlayerCap stat : stats) {
            if (stat.isUnlimited) {
                unlimitedFound = true;
            }
            totalCap += stat.maxAmount;
        }
        return new TownPlayerCap(totalCap, unlimitedFound);
    }

    @Override
    public Lang getStatName() {
        return Lang.PLAYER_CAP;
    }
}
