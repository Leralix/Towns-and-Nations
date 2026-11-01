package org.leralix.tan.data.upgrade.rewards.numeric;

import org.leralix.tan.data.upgrade.rewards.IndividualStat;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;

public abstract class NumericStat extends IndividualStat {

    protected final int maxAmount;
    protected final boolean isUnlimited;

    protected NumericStat(int maxAmount, boolean isUnlimited) {
        this.maxAmount = maxAmount;
        this.isUnlimited = isUnlimited;
    }

    public FilledLang getStatReward(LangType langType, int level, int maxLevel) {
        if(isUnlimited){
            if(level == 0){
                return Lang.UPGRADE_LINE_INFINITY_LOCKED.get(getStatName().translate(langType));
            }
            else {
                return Lang.UPGRADE_LINE_INFINITY_UNLOCKED.get(getStatName().translate(langType));
            }
        }
        else {
            if(level >= maxLevel){
                return Lang.UPGRADE_LINE_INT_MAX.get(
                        getStatName().translate(langType),
                        getMathSign(maxAmount * maxLevel)
                );
            }
            else {
                return Lang.UPGRADE_LINE_INT.get(
                        getStatName().translate(langType),
                        getMathSign(maxAmount * level),
                        getMathSign(maxAmount)
                );
            }
        }
    }


    public FilledLang getStatReward(LangType langType) {
        if(isUnlimited){
            return Lang.UPGRADE_LINE_INFINITY_UNLOCKED.get(getStatName().translate(langType));
        }
        else {
            return Lang.UPGRADE_LINE_INT_MAX.get(
                    getStatName().translate(langType),
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
