package org.leralix.tan.upgrade.rewards.bool;

import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.upgrade.rewards.AggregatableStat;

import java.util.List;

public class EnableMobBan extends BooleanStat implements AggregatableStat<EnableMobBan> {

    public EnableMobBan(){
        super(false);
    }

    public EnableMobBan(boolean state) {
        super(state);
    }

    @Override
    public EnableMobBan aggregate(List<EnableMobBan> stats) {
        for (EnableMobBan stat : stats) {
            if (stat.state) {
                return new EnableMobBan(true);
            }
        }
        return new EnableMobBan(false);
    }

    @Override
    public EnableMobBan scale(int factor) {
        // Upgrade is not bought
        if(factor == 0){
            return new EnableMobBan(false);
        }
        return this;
    }

    @Override
    public FilledLang getStatReward(LangType langType, int level, int maxLevel) {
        return getStatReward(langType, level, Lang.UNLOCK_MOB_BAN);
    }

    @Override
    public FilledLang getStatReward(LangType langType) {
        return getStatReward(langType, Lang.UNLOCK_MOB_BAN);
    }
}
