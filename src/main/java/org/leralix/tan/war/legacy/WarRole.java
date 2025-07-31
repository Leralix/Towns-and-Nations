package org.leralix.tan.war.legacy;

import org.leralix.tan.lang.Lang;

public enum WarRole {

    MAIN_ATTACKER(Lang.MAIN_ATTACKER_NAME.get()),
    MAIN_DEFENDER(Lang.MAIN_DEFENDER_NAME.get()),
    OTHER_ATTACKER(Lang.OTHER_ATTACKER_NAME.get()),
    OTHER_DEFENDER(Lang.OTHER_DEFENDER_NAME.get()),
    NEUTRAL(Lang.NEUTRAL_NAME.get());

    final String name;

    WarRole(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public WarRole reverse() {
        return switch (this) {
            case MAIN_ATTACKER -> MAIN_DEFENDER;
            case MAIN_DEFENDER -> MAIN_ATTACKER;
            case OTHER_ATTACKER -> OTHER_DEFENDER;
            case OTHER_DEFENDER -> OTHER_ATTACKER;
            case NEUTRAL -> NEUTRAL;
        };
    }
}
