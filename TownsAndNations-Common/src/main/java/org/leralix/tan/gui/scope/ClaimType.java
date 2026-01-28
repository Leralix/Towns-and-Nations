package org.leralix.tan.gui.scope;

import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;

public enum ClaimType {

    TOWN(Lang.MAP_TOWN, "town"),
    REGION(Lang.MAP_REGION, "region"),
    NATION(Lang.MAP_NATION, "nation");

    /**
     * The name displayed on the button
     */
    private final Lang buttonName;
    /**
     * The command associated with the button
     */
    private final String buttonCommand;
    /**
     * The next claim type in the cycle
     */
    private ClaimType nextType;

    ClaimType(Lang buttonName, String buttonCommand){
        this.buttonName = buttonName;
        this.buttonCommand = buttonCommand;
    }

    public String getName(LangType langType) {
        return buttonName.get(langType);
    }

    public String getTypeCommand() {
        return buttonCommand;
    }

    public ClaimType getNextType() {
        return nextType;
    }

    private void setNextType(ClaimType nextType) {
        this.nextType = nextType;
    }

    static {
        TOWN.setNextType(REGION);
        REGION.setNextType(NATION);
        NATION.setNextType(TOWN);
    }
}
