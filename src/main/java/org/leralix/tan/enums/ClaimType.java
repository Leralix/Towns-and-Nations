package org.leralix.tan.enums;

import org.leralix.tan.lang.Lang;

public enum ClaimType {

    TOWN(Lang.MAP_TOWN.get(), "TOWN"),
    REGION(Lang.MAP_REGION.get(), "REGION");

    private final String buttonName;
    private final String buttonCommand;
    private ClaimType nextType;

    ClaimType(String buttonName, String buttonCommand){
        this.buttonName = buttonName;
        this.buttonCommand = buttonCommand;
    }

    public String getName() {
        return buttonName;
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
        REGION.setNextType(TOWN);
    }
}
