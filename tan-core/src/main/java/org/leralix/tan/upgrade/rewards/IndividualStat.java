package org.leralix.tan.upgrade.rewards;

import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.LangType;

public abstract class IndividualStat {

  public abstract FilledLang getStatReward(LangType langType, int level, int maxLevel);

  public abstract FilledLang getStatReward(LangType langType);

  protected String getMathSign(int value) {
    if (value > 0) {
      return "+" + value;
    }
    return Integer.toString(value);
  }
}
