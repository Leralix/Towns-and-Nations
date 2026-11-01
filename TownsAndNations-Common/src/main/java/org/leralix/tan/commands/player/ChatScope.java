package org.leralix.tan.commands.player;

import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.Placeholder;

public enum ChatScope {
    CITY(Lang.CITY_SCOPE),
    REGION(Lang.REGION_SCOPE),
    NATION(Lang.NATION_SCOPE),
    ALLIANCE(Lang.ALLIANCE_SCOPE),
    GLOBAL(Lang.GLOBAL_SCOPE);

    private final Lang name;

    ChatScope(Lang name){
        this.name = name;
    }

    public Placeholder getName() {
        return name;
    }
}
