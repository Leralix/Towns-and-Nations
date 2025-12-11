package org.leralix.tan.upgrade.rewards.numeric;

import java.util.List;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.gui.service.requirements.IndividualRequirement;
import org.leralix.tan.gui.service.requirements.PropertyCapRequirement;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.upgrade.rewards.AggregatableStat;

public class LandmarkCap extends NumericStat implements AggregatableStat<LandmarkCap> {

  @SuppressWarnings("unused")
  public LandmarkCap() {
    super(0, false);
  }

  public LandmarkCap(int maxAmount, boolean isUnlimited) {
    super(maxAmount, isUnlimited);
  }

  @Override
  public LandmarkCap aggregate(List<LandmarkCap> stats) {

    int totalCap = 0;
    boolean unlimitedFound = false;
    for (LandmarkCap stat : stats) {
      if (stat.isUnlimited) {
        unlimitedFound = true;
      }
      totalCap += stat.maxAmount;
    }
    return new LandmarkCap(totalCap, unlimitedFound);
  }

  @Override
  public LandmarkCap scale(int factor) {
    return new LandmarkCap(maxAmount * factor, isUnlimited);
  }

  public IndividualRequirement getRequirement(TownData townData) {
    return new PropertyCapRequirement(townData, maxAmount);
  }

  @Override
  public FilledLang getStatReward(LangType langType, int level, int maxLevel) {
    return getStatReward(langType, level, maxLevel, Lang.LANDMARK_CAP);
  }

  @Override
  public FilledLang getStatReward(LangType langType) {
    return getStatReward(langType, Lang.LANDMARK_CAP);
  }
}
