package org.leralix.tan.wars.legacy;

import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.wars.War;

public class CreateAttackData {

  private final int minTime;

  private final int maxTime;

  private int selectedTime;

  private final War war;
  private final WarRole attackingSide;

  public CreateAttackData(War war, WarRole attackingSide) {

    this.minTime = Constants.getMinTimeBeforeAttack();
    this.maxTime = Constants.getMaxTimeBeforeAttack();

    this.selectedTime = (minTime + maxTime) / 2;
    this.war = war;
    this.attackingSide = attackingSide;
  }

  public void addDeltaDateTime(int deltaDateTime) {
    this.selectedTime += deltaDateTime;
    if (this.selectedTime < minTime) {
      this.selectedTime = minTime;
    }
    if (this.selectedTime > maxTime) {
      this.selectedTime = maxTime;
    }
  }

  public int getSelectedTime() {
    return selectedTime;
  }

  public War getWar() {
    return war;
  }

  public WarRole getAttackingSide() {
    return attackingSide;
  }
}
