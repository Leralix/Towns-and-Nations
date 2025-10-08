package org.leralix.tan.dataclass.property;

import org.leralix.tan.dataclass.ITanPlayer;

public abstract class AbstractOwner {

    /**
     * Used for serialisation
     */
    protected final OwnerType type;


    protected AbstractOwner(OwnerType type){
        this.type = type;
    }


    public abstract String getName();

    public abstract boolean canAccess(ITanPlayer tanPlayer);

    public abstract void addToBalance(double amount);
}
