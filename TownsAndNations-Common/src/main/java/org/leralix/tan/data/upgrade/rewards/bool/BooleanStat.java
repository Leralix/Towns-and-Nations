package org.leralix.tan.data.upgrade.rewards.bool;

import org.leralix.tan.data.upgrade.rewards.IndividualStat;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;

public abstract class BooleanStat extends IndividualStat {

    protected final boolean state;

    protected BooleanStat(boolean state) {
        this.state = state;
    }

    protected FilledLang getStatReward(LangType langType, int level) {
        if(level == 0){
            return Lang.UPGRADE_LINE_LOCKED_UNLOCKED.get(getStatName().get(langType));
        }
        else {
            return Lang.UPGRADE_LINE_UNLOCKED.get(getStatName().get(langType));
        }
    }

    public FilledLang getStatReward(LangType langType) {
        if(state){
            return Lang.UPGRADE_LINE_UNLOCKED.get(getStatName().get(langType));
        }
        else {
            return Lang.UPGRADE_LINE_LOCKED.get(getStatName().get(langType));
        }
    }

    public boolean isEnabled() {
        return state;
    }
}
