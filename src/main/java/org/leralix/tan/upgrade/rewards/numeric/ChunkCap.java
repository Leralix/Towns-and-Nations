package org.leralix.tan.upgrade.rewards.numeric;

import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.gui.service.requirements.IndividualRequirement;
import org.leralix.tan.gui.service.requirements.PropertyCapRequirement;
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

    public IndividualRequirement getRequirement(TownData townData) {
        return new PropertyCapRequirement(townData, maxAmount);
    }

    @Override
    public String getStatReward(LangType langType, int level, int maxLevel) {
        return getStatReward(langType, level, maxLevel, Lang.CHUNK_CAP);
    }

    @Override
    public String getStatReward(LangType langType) {
        return getStatReward(langType, Lang.CHUNK_CAP);
    }
}
