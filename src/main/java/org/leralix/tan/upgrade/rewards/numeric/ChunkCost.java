package org.leralix.tan.upgrade.rewards.numeric;

import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.upgrade.rewards.AggregatableStat;

import java.util.List;

public class ChunkCost extends NumericStat implements AggregatableStat<ChunkCost> {

    public ChunkCost(){
        super(0, false);
    }

    public ChunkCost(int maxAmount, boolean isUnlimited) {
        super(maxAmount, isUnlimited);
    }



    @Override
    public ChunkCost aggregate(List<ChunkCost> stats) {
        int totalCap = 0;
        boolean unlimitedFound = false;
        for (ChunkCost stat : stats) {
            if (stat.isUnlimited) {
                unlimitedFound = true;
            }
            totalCap += stat.maxAmount;
        }
        return new ChunkCost(totalCap, unlimitedFound);
    }

    @Override
    public ChunkCost scale(int factor) {
        return new ChunkCost(maxAmount * factor, isUnlimited);
    }

    @Override
    public FilledLang getStatReward(LangType langType, int level, int maxLevel) {
        return getStatReward(langType, level, maxLevel, Lang.CHUNK_COST);
    }

    @Override
    public FilledLang getStatReward(LangType langType) {
        return getStatReward(langType, Lang.CHUNK_COST);
    }

    public int getCost() {
        return maxAmount;
    }
}
