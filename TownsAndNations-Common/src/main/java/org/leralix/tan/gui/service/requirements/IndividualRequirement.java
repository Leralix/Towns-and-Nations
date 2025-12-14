package org.leralix.tan.gui.service.requirements;

import org.leralix.tan.lang.LangType;

public abstract class IndividualRequirement {

    /**
     * @return the requirement line with basic information.
     */
    public abstract String getLine(LangType langType);

    /**
     * @return true if conditions are met, false otherwise.
     */
    public abstract boolean isInvalid();

}
