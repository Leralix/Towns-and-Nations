package org.leralix.tan.upgrade.rewards.numeric;

import java.util.List;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.gui.service.requirements.IndividualRequirement;
import org.leralix.tan.gui.service.requirements.PropertyCapRequirement;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.upgrade.rewards.AggregatableStat;

public class PropertyCap extends NumericStat implements AggregatableStat<PropertyCap> {

  /** Default constructor Needed to create an empty stat. Do not remove */
  @SuppressWarnings("unused")
  public PropertyCap() {
    super(0, false);
  }

  public PropertyCap(int maxAmount, boolean isUnlimited) {
    super(maxAmount, isUnlimited);
  }

  @Override
  public PropertyCap scale(int factor) {
    return new PropertyCap(maxAmount * factor, isUnlimited);
  }

  @Override
  public PropertyCap aggregate(List<PropertyCap> stats) {

    int totalCap = 0;
    boolean unlimitedFound = false;
    for (PropertyCap stat : stats) {
      if (stat.isUnlimited) {
        unlimitedFound = true;
      }
      totalCap += stat.maxAmount;
    }
    return new PropertyCap(totalCap, unlimitedFound);
  }

  public IndividualRequirement getRequirement(TownData townData) {
    return new PropertyCapRequirement(townData, maxAmount);
  }

  @Override
  public FilledLang getStatReward(LangType langType, int level, int maxLevel) {
    return getStatReward(langType, level, maxLevel, Lang.PROPERTY_CAP);
  }

  @Override
  public FilledLang getStatReward(LangType langType) {
    return getStatReward(langType, Lang.PROPERTY_CAP);
  }
}
