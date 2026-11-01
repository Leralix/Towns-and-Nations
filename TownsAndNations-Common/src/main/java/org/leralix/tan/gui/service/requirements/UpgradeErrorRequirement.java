package org.leralix.tan.gui.service.requirements;

import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.Placeholder;

public class UpgradeErrorRequirement extends IndividualRequirement {


    @Override
    public Placeholder getLine() {
        return Lang.GENERIC_ERROR;
    }

    @Override
    public boolean isInvalid() {
        return false;
    }
}
