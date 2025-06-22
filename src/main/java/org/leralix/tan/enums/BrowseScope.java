package org.leralix.tan.enums;

import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.lang.Lang;

public enum BrowseScope {

    ALL(Lang.BROWSE_ALL_NAME, null),
    TOWNS(Lang.BROWSE_TOWNS_NAME, null),
    REGIONS( Lang.BROWSE_REGIONS_NAME, null);

    private final Lang name;
    private BrowseScope nextScope;

    BrowseScope(Lang name, BrowseScope nextScope) {
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

    public String getName(ITanPlayer tanPlayer) {
        return name.get(tanPlayer);
    }
}
