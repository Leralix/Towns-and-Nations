package org.leralix.tan.data.upgrade.rewards.numeric;

import org.leralix.tan.data.upgrade.rewards.IndividualStat;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;

public abstract class DoubleNumericStat extends IndividualStat {

    protected final double amount;

    protected DoubleNumericStat(double amount) {
        this.amount = amount;
    }

    public FilledLang getStatReward(LangType langType, int level, int maxLevel) {
        if (level >= maxLevel) {
            return Lang.UPGRADE_LINE_INT_MAX.get(
                    getStatName().get(langType),
                    getMathSign(amount * maxLevel)
            );
        } else {
            return Lang.UPGRADE_LINE_INT.get(
                    getStatName().get(langType),
                    getMathSign(amount * level),
                    getMathSign(amount)
            );
        }

    }


    public FilledLang getStatReward(LangType langType) {
        return Lang.UPGRADE_LINE_INT_MAX.get(
                getStatName().get(langType),
                getMathSign(amount));
    }

    public double getAmount() {
        return amount;
    }
}
