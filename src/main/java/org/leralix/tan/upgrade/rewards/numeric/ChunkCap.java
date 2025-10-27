package org.leralix.tan.upgrade.rewards.numeric;

import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.upgrade.rewards.AggregatableStat;

import java.util.List;

public class ChunkCap extends NumericStat implements AggregatableStat<ChunkCap> {

    /**
     * Default constructor
     * Needed to create an empty stat. Do not remove
     */
    @SuppressWarnings("unused")
    public ChunkCap(){
        super(0, false);
    }

    public ChunkCap(int maxAmount, boolean isUnlimited) {
        super(maxAmount, isUnlimited);
    }

    public String getMaxNumberOfChunks() {
        if (isUnlimited) {
            return "∞";
        } else {
            return Integer.toString(maxAmount);
        }
    }

    @Override
    public ChunkCap aggregate(List<ChunkCap> stats) {

        int totalCap = 0;
        boolean unlimitedFound = false;
        for (ChunkCap stat : stats) {
            if (stat.isUnlimited) {
                unlimitedFound = true;
            }
            totalCap += stat.maxAmount;
        }
        return new ChunkCap(totalCap, unlimitedFound);
    }

    @Override
    public ChunkCap scale(int factor) {
        return new ChunkCap(maxAmount * factor, isUnlimited);
    }

    @Override
    public FilledLang getStatReward(LangType langType, int level, int maxLevel) {
        return getStatReward(langType, level, maxLevel, Lang.CHUNK_CAP);
    }

    @Override
    public FilledLang getStatReward(LangType langType) {
        return getStatReward(langType, Lang.CHUNK_CAP);
    }
}
