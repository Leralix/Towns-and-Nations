package org.leralix.tan.enums;

import org.leralix.tan.lang.Lang;

public enum ClaimAction {

    CLAIM(Lang.CLAIM_BUTTON.get(), "CLAIM"),
    UNCLAIM(Lang.UNCLAIM_BUTTON.get(), "UNCLAIM");

    private final String buttonName;
    private final String buttonCommand;
    private ClaimAction nextClaim;

    ClaimAction(String buttonName, String buttonCommand){
        this.buttonName = buttonName;
        this.buttonCommand = buttonCommand;
    }

    public String getName() {
        return buttonName;
    }

    public String getTypeCommand() {
        return buttonCommand;
    }

    public ClaimAction getNextType() {
        return nextClaim;
    }

    private void setNextType(ClaimAction nextType) {
        this.nextClaim = nextType;
    }

    static {
        CLAIM.setNextType(UNCLAIM);
        UNCLAIM.setNextType(CLAIM);
    }
}
