package org.leralix.tan.gui.service.requirements;

import org.jetbrains.annotations.Nullable;
import org.leralix.tan.lang.Placeholder;

public abstract class IndividualRequirement {

    /**
     * @return the requirement line with basic information.
     */
    public abstract @Nullable Placeholder getLine();

    /**
     * @return true if conditions are met, false otherwise.
     */
    public abstract boolean isInvalid();

}
