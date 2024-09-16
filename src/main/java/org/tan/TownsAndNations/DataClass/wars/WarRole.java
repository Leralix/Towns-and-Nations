package org.tan.TownsAndNations.DataClass.wars;

import org.tan.TownsAndNations.Lang.Lang;

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
}
