package org.leralix.tan.upgrade.rewards;

import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;

public abstract class IndividualStat {

    /**
     * Get the stat reward description. Use it when displaying in an upgrade.
     * @param langType  the language
     * @param level     the current level of the stat
     * @param maxLevel  the maximum level of the stat
     * @return the stat reward description.
     */
    public abstract FilledLang getStatReward(LangType langType, int level, int maxLevel);

    /**
     * Get the stat reward description. Use it when displaying a territory current stat overview.
     * @param langType  the language
     * @return the stat reward description.
     */
    public abstract FilledLang getStatReward(LangType langType);

    /**
     * @return The name of the Stat
     */
    public abstract Lang getStatName();

    protected String getMathSign(int value){
        if(value > 0){
            return "+" + value;
        }
        return Integer.toString(value);
    }

    protected String getMathSign(double value){
        if(value > 0){
            return "+" + value;
        }
        return Double.toString(value);
    }
}
