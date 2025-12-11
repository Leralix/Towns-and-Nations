package org.leralix.tan.upgrade.rewards.numeric;

import java.util.List;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.upgrade.rewards.AggregatableStat;

public class TownPlayerCap extends NumericStat implements AggregatableStat<TownPlayerCap> {

  @SuppressWarnings("unused")
  public TownPlayerCap() {
    super(0, false);
  }

  public TownPlayerCap(int maxAmount, boolean isUnlimited) {
    super(maxAmount, isUnlimited);
  }

  @Override
  public TownPlayerCap scale(int factor) {
    return new TownPlayerCap(maxAmount * factor, isUnlimited);
  }

  @Override
  public TownPlayerCap aggregate(List<TownPlayerCap> stats) {
    int totalCap = 0;
    boolean unlimitedFound = false;
    for (TownPlayerCap stat : stats) {
      if (stat.isUnlimited) {
        unlimitedFound = true;
      }
      totalCap += stat.maxAmount;
    }
    return new TownPlayerCap(totalCap, unlimitedFound);
  }

  @Override
  public FilledLang getStatReward(LangType langType, int level, int maxLevel) {
    return getStatReward(langType, level, maxLevel, Lang.PLAYER_CAP);
  }

  @Override
  public FilledLang getStatReward(LangType langType) {
    return getStatReward(langType, Lang.PLAYER_CAP);
  }
}
