package org.leralix.tan.gui.service.requirements;

import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;


public class MaxLevelReachedRequirement extends IndividualRequirement{

    @Override
    public String getLine(LangType langType) {
        return Lang.MAX_LEVEL_REACHED.get(langType);
    }

    @Override
    public boolean isInvalid() {
        return true;
    }

}
