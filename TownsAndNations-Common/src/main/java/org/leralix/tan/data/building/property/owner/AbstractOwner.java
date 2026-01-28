package org.leralix.tan.data.building.property.owner;

import org.tan.api.interfaces.TanOwner;

public abstract class AbstractOwner implements TanOwner {

    /**
     * Used for serialisation
     */
    protected final OwnerType type;

    protected AbstractOwner(OwnerType type){
        this.type = type;
    }

}
