package org.leralix.tan.data.upgrade.rewards.numeric;

import org.leralix.tan.data.upgrade.rewards.AggregatableStat;
import org.leralix.tan.lang.Lang;

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
    public Lang getStatName() {
        return Lang.CHUNK_COST;
    }

    public int getCost() {
        return maxAmount;
    }
}
