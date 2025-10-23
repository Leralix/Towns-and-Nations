package org.leralix.tan.upgrade.rewards.bool;

import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.upgrade.rewards.IndividualStat;

public abstract class BooleanStat extends IndividualStat {

    protected final boolean state;

    protected BooleanStat(boolean state) {
        this.state = state;
    }

    protected String getStatReward(LangType langType, int level, int maxLevel, Lang statName) {
        if(level == 0){
            return Lang.UPGRADE_LINE_LOCKED_UNLOCKED.get(langType, statName.get(langType));
        }
        else {
            return Lang.UPGRADE_LINE_UNLOCKED.get(langType, statName.get(langType));
        }
    }

    protected String getStatReward(LangType langType, Lang statName) {
        if(state){
            return Lang.UPGRADE_LINE_UNLOCKED.get(langType, statName.get(langType));
        }
        else {
            return Lang.UPGRADE_LINE_LOCKED.get(langType, statName.get(langType));
        }
    }


}
