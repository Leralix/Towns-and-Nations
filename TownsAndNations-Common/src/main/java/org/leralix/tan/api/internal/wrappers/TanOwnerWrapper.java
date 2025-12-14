package org.leralix.tan.api.internal.wrappers;

import org.leralix.tan.dataclass.property.AbstractOwner;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.tan.api.interfaces.TanOwner;
import org.tan.api.interfaces.TanPlayer;

public class TanOwnerWrapper implements TanOwner {

    private final AbstractOwner owner;

    private TanOwnerWrapper(AbstractOwner owner){
        this.owner = owner;
    }

    public static TanOwnerWrapper of(AbstractOwner owner) {
        if (owner == null) {
            return null;
        }
        return new TanOwnerWrapper(owner);
    }

    /**
     * @return  The owner of the property
     */
    @Override
    public String getName() {
        return owner.getName();
    }

    @Override
    public String getColoredName() {
        return owner.getColoredName();
    }

    /**
     *
     * @param tanPlayer the player trying to access.
     * @return  the owner
     */
    @Override
    public boolean canAccess(TanPlayer tanPlayer) {
        return owner.canAccess(PlayerDataStorage.getInstance().get(tanPlayer.getUUID()));
    }

    @Override
    public void addToBalance(double amount) {
        owner.addToBalance(amount);
    }

    @Override
    public String getID() {
        return owner.getID();
    }
}
