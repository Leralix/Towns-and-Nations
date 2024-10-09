package org.leralix.tan.enums;

import org.leralix.tan.Lang.Lang;

public enum BrowseScope {

    ALL(Lang.BROWSE_ALL_NAME.get(), null),
    TOWNS(Lang.BROWSE_TOWNS_NAME.get(), null),
    REGIONS( Lang.BROWSE_REGIONS_NAME.get(), null);

    private final String name;
    private BrowseScope nextScope;

    BrowseScope(String name, BrowseScope nextScope) {
        this.name = name;
        this.nextScope = nextScope;
    }

    public BrowseScope getNextScope() {
        return nextScope;
    }

    static {
        ALL.nextScope = TOWNS;
        TOWNS.nextScope = REGIONS;
        REGIONS.nextScope = ALL;
    }

    public String getName() {
        return name;
    }
}
