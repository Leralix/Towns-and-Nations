package org.leralix.tan.upgrade.rewards.percentage;

import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.upgrade.rewards.AggregatableStat;
import org.leralix.tan.upgrade.rewards.IndividualStat;

import java.util.List;

public class LandmarkBonus extends IndividualStat implements AggregatableStat<LandmarkBonus> {

    private final double percentage;

    @SuppressWarnings("unused")
    public LandmarkBonus(){
        this.percentage = 0;
    }

    public LandmarkBonus(double percentage) {
        this.percentage = percentage;
    }

    @Override
    public LandmarkBonus aggregate(List<LandmarkBonus> stats) {
        double totalPercentage = 1;
        for (LandmarkBonus stat : stats) {
            totalPercentage *= (1. + stat.percentage);
        }
        return new LandmarkBonus(totalPercentage - 1);
    }

    @Override
    public LandmarkBonus scale(int factor) {
        return new LandmarkBonus(percentage * factor);
    }

    @Override
    public String getStatReward(LangType langType, int level, int maxLevel) {
        int percentValue = (int) (percentage * level * 100);
        int percentValueNext = (int) (percentage * (level + 1) * 100);

        if(level >= maxLevel){
            return Lang.UPGRADE_LINE_PERCENT_MAX.get(langType, Lang.LANDMARK_BONUS.get(langType), Integer.toString(percentValue));
        } else {
            return Lang.UPGRADE_LINE_PERCENT.get(langType, Lang.LANDMARK_BONUS.get(langType), Integer.toString(percentValue), Integer.toString(percentValueNext));
        }
    }

    @Override
    public String getStatReward(LangType langType) {
        int percentValue = (int) ((1. + percentage) * 100);
        return Lang.UPGRADE_LINE_PERCENT_MAX.get(langType, Lang.LANDMARK_BONUS.get(langType), Double.toString(percentValue));
    }

    public double multiply(double baseValue) {
        return baseValue * (1. + percentage);
    }
}
