package org.leralix.tan.gui.service.requirements;

import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.Placeholder;


public class MaxLevelReachedRequirement extends IndividualRequirement{

    @Override
    public Placeholder getLine() {
        return Lang.MAX_LEVEL_REACHED;
    }

    @Override
    public boolean isInvalid() {
        return true;
    }

}
