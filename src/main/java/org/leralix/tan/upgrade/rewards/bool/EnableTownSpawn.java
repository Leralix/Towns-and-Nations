package org.leralix.tan.upgrade.rewards.bool;

import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.upgrade.rewards.AggregatableStat;

import java.util.List;

public class EnableTownSpawn extends BooleanStat implements AggregatableStat<EnableTownSpawn> {

    public EnableTownSpawn(){
        super(false);
    }

    public EnableTownSpawn(boolean state) {
        super(state);
    }

    @Override
    public EnableTownSpawn aggregate(List<EnableTownSpawn> stats) {
        for (EnableTownSpawn stat : stats) {
            if (stat.state) {
                return new EnableTownSpawn(true);
            }
        }
        return new EnableTownSpawn(false);
    }

    @Override
    public EnableTownSpawn scale(int factor) {
        // Upgrade is not bought
        if(factor == 0){
            return new EnableTownSpawn(false);
        }
        return this;
    }

    @Override
    public FilledLang getStatReward(LangType langType, int level, int maxLevel) {
        return getStatReward(langType, level, Lang.UNLOCK_TOWN_SPAWN);
    }

    @Override
    public FilledLang getStatReward(LangType langType) {
        return getStatReward(langType, Lang.UNLOCK_TOWN_SPAWN);
    }
}
