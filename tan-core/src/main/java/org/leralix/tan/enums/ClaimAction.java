package org.leralix.tan.enums;

import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;

public enum ClaimAction {
  CLAIM(Lang.CLAIM_BUTTON, "CLAIM"),
  UNCLAIM(Lang.UNCLAIM_BUTTON, "UNCLAIM");

  private final Lang buttonName;
  private final String buttonCommand;
  private ClaimAction nextClaim;

  ClaimAction(Lang buttonName, String buttonCommand) {
    this.buttonName = buttonName;
    this.buttonCommand = buttonCommand;
  }

  public String getName(LangType langType) {
    return buttonName.get(langType);
  }

  public String getTypeCommand() {
    return buttonCommand;
  }

  public ClaimAction getNextType() {
    return nextClaim;
  }

  private void setNextType(ClaimAction nextType) {
    this.nextClaim = nextType;
  }

  static {
    CLAIM.setNextType(UNCLAIM);
    UNCLAIM.setNextType(CLAIM);
  }
}
