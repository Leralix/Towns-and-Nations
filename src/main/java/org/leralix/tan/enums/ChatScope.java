package org.leralix.tan.enums;

import org.leralix.tan.lang.Lang;

public enum ChatScope {
    CITY(Lang.CITY_SCOPE),
    REGION(Lang.REGION_SCOPE),
    ALLIANCE(Lang.ALLIANCE_SCOPE),
    GLOBAL(Lang.GLOBAL_SCOPE);

    private final Lang name;

    ChatScope(Lang name){
        this.name = name;
    }

    public Lang getName() {
        return name;
    }
}
