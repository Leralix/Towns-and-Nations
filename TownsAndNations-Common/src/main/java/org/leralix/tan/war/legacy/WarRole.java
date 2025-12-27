package org.leralix.tan.war.legacy;

import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;

public enum WarRole {

    MAIN_ATTACKER(Lang.MAIN_ATTACKER_NAME),
    MAIN_DEFENDER(Lang.MAIN_DEFENDER_NAME),
    OTHER_ATTACKER(Lang.OTHER_ATTACKER_NAME),
    OTHER_DEFENDER(Lang.OTHER_DEFENDER_NAME),
    NEUTRAL(Lang.NEUTRAL_NAME);

    final Lang name;

    WarRole(Lang name) {
        this.name = name;
    }

    public String getName(LangType langType) {
        return name.get(langType);
    }

    public WarRole opposite() {
        return switch (this) {
            case MAIN_ATTACKER -> MAIN_DEFENDER;
            case MAIN_DEFENDER -> MAIN_ATTACKER;
            case OTHER_ATTACKER -> OTHER_DEFENDER;
            case OTHER_DEFENDER -> OTHER_ATTACKER;
            case NEUTRAL -> NEUTRAL;
        };
    }

    public boolean isMain() {
        return switch (this){
            case MAIN_ATTACKER, MAIN_DEFENDER -> true;
            case OTHER_ATTACKER, OTHER_DEFENDER, NEUTRAL -> false;
        };
    }

    public boolean isSecondary() {
        return switch (this){
            case OTHER_ATTACKER, OTHER_DEFENDER -> true;
            case MAIN_ATTACKER, MAIN_DEFENDER, NEUTRAL -> false;
        };
    }

    public boolean isOpposite(WarRole otherWarRole) {
        return switch (this){
            case MAIN_ATTACKER, OTHER_ATTACKER ->
                    otherWarRole == WarRole.MAIN_DEFENDER || otherWarRole == WarRole.OTHER_DEFENDER;
            case MAIN_DEFENDER, OTHER_DEFENDER ->
                    otherWarRole == WarRole.MAIN_ATTACKER || otherWarRole == WarRole.OTHER_ATTACKER;
            case NEUTRAL -> false;
        };
    }
}
