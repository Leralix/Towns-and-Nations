package org.tan.api.interfaces;

import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.tan.api.enums.ETownPermission;

import java.util.Collection;
import java.util.UUID;

public interface TanTerritory {

    /**
     * @return the ID of the territory
     */
    String getID();

    /**
     * @return the name of the territory
     */
    String getName();

    /**
     * Set the name of the territory
     * @param name the new name of the territory
     */
    void setName(String name);

    /**
     * @return the description of the territory
     */
    String getDescription();

    /**
     * Set the description of the territory
     * @param description the new description of the territory
     */
    void setDescription(String description);

    /**
     * @return the owner of the territory
     */
    TanPlayer getOwner();

    /**
     * @return the UUID of the owner of the territory
     */
    UUID getOwnerUUID();

    /**
     * @return the creation date of the territory in Unix timestamp format
     */
    Long getCreationDate();

    /**
     * @return the icon of the territory
     */
    ItemStack getIcon();

    /**
     * Get the color of the territory
     * @return the color of the territory
     */
    Color getColor();

    /**
     * Set the color of the territory
     * @param color the new color of the territory
     */
    void setColor(Color color);

    /**
     * @return the number of claimed chunks in the territory
     */
    int getNumberOfClaimedChunk();

    /**
     * @return The number of claimed chunks of this territory
     */
    Collection<TanClaimedChunk> getClaimedChunks();

    /**
     * @return the members of the territory
     */
    Collection<TanPlayer> getMembers();

    /**
     * @return the vassals of the territory
     */
    Collection<TanTerritory> getVassals();

    /**
     * @return the overlord of the territory
     */
    boolean haveOverlord();

    /**
     * @return the overlord of the territory
     */
    TanTerritory getOverlord();

    /**
     * Check if the player has the permission to do the action in the territory
     * If the player is not a member of the territory, it will always return false.
     * @param player        The player to check
     * @param permission    The specific permission to check
     * @return              True if the player has the permission, false otherwise
     */
    boolean canPlayerDoAction(TanPlayer player, ETownPermission permission);


}
