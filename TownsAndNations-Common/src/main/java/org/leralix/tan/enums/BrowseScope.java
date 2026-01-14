package org.leralix.tan.enums;

import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.utils.deprecated.DisplayableEnum;

public enum BrowseScope implements DisplayableEnum {

    ALL(Lang.BROWSE_ALL_NAME),
    TOWNS(Lang.BROWSE_TOWNS_NAME),
    REGIONS(Lang.BROWSE_REGIONS_NAME),
    NATIONS(Lang.BROWSE_NATIONS_NAME);

    private final Lang name;

    BrowseScope(Lang name) {
        this.name = name;
    }

    @Override
    public String getDisplayName(LangType langType) {
        return name.get(langType);
    }
}
