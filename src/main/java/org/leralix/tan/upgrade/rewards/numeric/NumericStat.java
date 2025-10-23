package org.leralix.tan.upgrade.rewards.numeric;

import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.upgrade.rewards.IndividualStat;

public abstract class NumericStat extends IndividualStat {

    protected final int maxAmount;
    protected final boolean isUnlimited;

    protected NumericStat(int maxAmount, boolean isUnlimited) {
        this.maxAmount = maxAmount;
        this.isUnlimited = isUnlimited;
    }

    protected String getMathSign(int value){
        if(value > 0){
            return "+" + value;
        }
        return Integer.toString(value);
    }

    protected String getStatReward(LangType langType, int level, int maxLevel, Lang statName) {
        if(isUnlimited){
            if(level == 0){
                return Lang.UPGRADE_LINE_INFINITY_LOCKED.get(langType, statName.get(langType));
            }
            else {
                return Lang.UPGRADE_LINE_INFINITY_UNLOCKED.get(langType, statName.get(langType));
            }
        }
        else {
            if(level >= maxLevel){
                return Lang.UPGRADE_LINE_INT_MAX.get(langType,
                        statName.get(langType),
                        getMathSign(maxAmount * maxLevel)
                );
            }
            else {
                return Lang.UPGRADE_LINE_INT.get(langType,
                        statName.get(langType),
                        getMathSign(maxAmount * level),
                        getMathSign(maxAmount)
                );
            }
        }
    }


    protected String getStatReward(LangType langType, Lang statName) {
        if(isUnlimited){
            return Lang.UPGRADE_LINE_INFINITY_UNLOCKED.get(langType, statName.get(langType));
        }
        else {
            return Lang.UPGRADE_LINE_INT_MAX.get(langType,
                    statName.get(langType),
                    getMathSign(maxAmount));
        }
    }

    public boolean canDoAction(int value) {
        if (isUnlimited) {
            return true;
        }
        return value <= maxAmount;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public boolean isUnlimited() {
        return isUnlimited;
    }
}
