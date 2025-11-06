package org.leralix.tan.gui.service.requirements;

import org.leralix.tan.lang.LangType;

public class UpgradeErrorRequirement extends IndividualRequirement {

  @Override
  public String getLine(LangType langType) {
    return "Error";
  }

  @Override
  public boolean isInvalid() {
    return false;
  }
}
