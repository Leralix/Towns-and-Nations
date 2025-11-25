package org.leralix.tan.upgrade.rewards.numeric;

import org.leralix.tan.lang.Lang;
import org.leralix.tan.upgrade.rewards.AggregatableStat;
import org.leralix.tan.utils.text.NumberUtil;

import java.util.List;

public class ChunkUpkeepCost extends DoubleNumericStat implements AggregatableStat<ChunkUpkeepCost> {

    @SuppressWarnings("unused")
    public ChunkUpkeepCost(){
        super(0);
    }

    public ChunkUpkeepCost(double upkeepCost) {
        super(upkeepCost);
    }

    @Override
    public Lang getStatName() {
       return Lang.CHUNK_UPKEEP_COST;
    }

    @Override
    public ChunkUpkeepCost aggregate(List<ChunkUpkeepCost> stats) {
        double value = 0.;
        for (ChunkUpkeepCost stat : stats) {
            value += stat.amount;
        }
        return new ChunkUpkeepCost(value);
    }

    @Override
    public ChunkUpkeepCost scale(int factor) {
        return new ChunkUpkeepCost(amount * factor);
    }

    public double getCost() {
        return NumberUtil.roundWithDigits(Math.max(0, amount), 5);
    }
}
