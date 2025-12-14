package org.tan.api.interfaces;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public interface TanLandmark {

    /**
     * @return The ID of the landmark.
     */
    String getID();

    /**
     * @return The name of the landmark.
     */
    String getName();

    /**
     * Set the name of the landmark.
     * @param name The name of the landmark.
     */
    void setName(String name);

    /**
     * @return The location of the landmark.
     */
    Location getLocation();

    /**
     * Set the quantity of items produced each day by the landmark.
     * @param quantity The quantity of items produced each day by the landmark.
     */
    void setQuantity(int quantity);

    /**
     * @return The quantity of items produced each day by the landmark.
     */
    int getQuantity();

    /**
     * Set the item produced by the landmark.
     * @param item The item produced by the landmark.
     */
    void setItem(ItemStack item);

    /**
     * @return The item produced by the landmark each day.
     */
    ItemStack getItem();

    /**
     * @return True if the landmark is owned by a territory, false otherwise.
     */
    boolean isOwned();

    /**
     * @return The owner of the landmark. If the landmark is not owned, null will be returned.
     */
    TanTerritory getOwner();

    /**
     * Remove ownership of the landmark. If the landmark is not owned, nothing will happen.
     */
    void removeOwnership();

    /**
     * Set the owner of the landmark. If the landmark is already owned, the ownership will be transferred.
     * @param newOwner The UUID of the new owner of the landmark.
     */
    void setOwner(UUID newOwner);

    /**
     * Set the owner of the landmark. If the landmark is already owned, the ownership will be transferred.
     * @param newOwner The new owner of the landmark.
     */
    void setOwner(TanTerritory newOwner);

}
