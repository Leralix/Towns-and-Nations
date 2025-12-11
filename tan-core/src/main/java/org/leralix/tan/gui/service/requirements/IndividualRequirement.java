package org.leralix.tan.gui.service.requirements;

import org.leralix.tan.lang.LangType;

public abstract class IndividualRequirement {

  public abstract String getLine(LangType langType);

  public abstract boolean isInvalid();
}
