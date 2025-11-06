package org.leralix.tan.upgrade.rewards.bool;

import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.upgrade.rewards.IndividualStat;

public abstract class BooleanStat extends IndividualStat {

  protected final boolean state;

  protected BooleanStat(boolean state) {
    this.state = state;
  }

  protected FilledLang getStatReward(LangType langType, int level, Lang statName) {
    if (level == 0) {
      return Lang.UPGRADE_LINE_LOCKED_UNLOCKED.get(statName.get(langType));
    } else {
      return Lang.UPGRADE_LINE_UNLOCKED.get(statName.get(langType));
    }
  }

  protected FilledLang getStatReward(LangType langType, Lang statName) {
    if (state) {
      return Lang.UPGRADE_LINE_UNLOCKED.get(statName.get(langType));
    } else {
      return Lang.UPGRADE_LINE_LOCKED.get(statName.get(langType));
    }
  }

  public boolean isEnabled() {
    return state;
  }
}
