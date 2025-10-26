package org.leralix.tan.upgrade.rewards;

import org.leralix.tan.lang.LangType;

public abstract class IndividualStat {

    /**
     * Get the stat reward description. Use it when displaying in an upgrade.
     * @param langType  the language
     * @param level     the current level of the stat
     * @param maxLevel  the maximum level of the stat
     * @return the stat reward description.
     */
    public abstract String getStatReward(LangType langType, int level, int maxLevel);

    /**
     * Get the stat reward description. Use it when displaying a territory current stat overview.
     * @param langType  the language
     * @return the stat reward description.
     */
    public abstract String getStatReward(LangType langType);

    protected String getMathSign(int value){
        if(value > 0){
            return "+" + value;
        }
        return Integer.toString(value);
    }
}
