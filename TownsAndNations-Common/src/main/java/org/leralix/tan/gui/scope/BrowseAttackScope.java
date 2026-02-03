package org.leralix.tan.gui.scope;

import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.war.PlannedAttack;

public enum BrowseAttackScope implements DisplayableEnum {

    ALL_ATTACKS(Lang.ALL_ATTACK_SCOPE),
    UNFINISHED_ONLY(Lang.UNFINISHED_ATTACK_SCOPE);

    private final Lang name;

    BrowseAttackScope(Lang name) {
        this.name = name;
    }

    public boolean allowAttack(PlannedAttack plannedAttack) {
        return switch (this){
            case ALL_ATTACKS -> true;
            case UNFINISHED_ONLY -> !plannedAttack.isFinished();
        };
    }

    @Override
    public String getDisplayName(LangType langType) {
        return name.get(langType);
    }
}
