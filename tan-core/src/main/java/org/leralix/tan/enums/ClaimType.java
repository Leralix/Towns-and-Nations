package org.leralix.tan.enums;

import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;

public enum ClaimType {
  TOWN(Lang.MAP_TOWN, "TOWN"),
  REGION(Lang.MAP_REGION, "REGION");

  private final Lang buttonName;
  private final String buttonCommand;
  private ClaimType nextType;

  ClaimType(Lang buttonName, String buttonCommand) {
    this.buttonName = buttonName;
    this.buttonCommand = buttonCommand;
  }

  public String getName(LangType langType) {
    return buttonName.get(langType);
  }

  public String getTypeCommand() {
    return buttonCommand;
  }

  public ClaimType getNextType() {
    return nextType;
  }

  private void setNextType(ClaimType nextType) {
    this.nextType = nextType;
  }

  static {
    TOWN.setNextType(REGION);
    REGION.setNextType(TOWN);
  }
}
