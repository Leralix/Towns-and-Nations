package org.tan.api.interfaces;

public interface TanOwner {

    String getName();

    String getColoredName();

    boolean canAccess(TanPlayer tanPlayer);

    void addToBalance(double amount);

    String getID();

}
