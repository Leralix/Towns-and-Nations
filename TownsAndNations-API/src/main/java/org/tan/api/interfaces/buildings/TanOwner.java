package org.tan.api.interfaces.buildings;

import org.tan.api.interfaces.TanPlayer;

public interface TanOwner {

    String getName();

    String getColoredName();

    boolean canAccess(TanPlayer tanPlayer);

    void addToBalance(double amount);

    String getID();

}
