package org.tan.api.interfaces.territory;

import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.tan.api.enums.TerritoryPermission;
import org.tan.api.interfaces.TanPlayer;
import org.tan.api.interfaces.chunk.TanClaimedChunk;

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
     * @return the name of the territory with its chunk color applied
     */
    String getColoredName();

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
    UUID getLeaderID();

    /**
     * @return the creation date of the territory in Unix timestamp format
     */
    long getCreationDate();

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
     * @return the chunk color in hex format (#RRGGBB)
     */
    String getChunkColorInHex();

    //TODO : uncomment this method when TownData is fixed
//    /**
//     * @return the overlord of the territory
//     */
//    Optional<TanTerritory> getOverlord();

    /**
     * Check if the player has the permission to do the action in the territory
     * If the player is not a member of the territory, it will always return false.
     * @param player        The player to check
     * @param permission    The specific permission to check
     * @return              True if the player has the permission, false otherwise
     */
    boolean canPlayerDoAction(TanPlayer player, TerritoryPermission permission);

    /**
     * Check if the player is in the territory
     * @param player    The player to check
     * @return  true if the player is a member of this territory, false otherwise
     */
    boolean isPlayerIn(Player player);

    /**
     * Check if the player has the specified permission in the territory
     * @param player            The player to check
     * @param rolePermission    Role permission to check
     * @return  true if the player has the specified permission, false otherwise
     */
    boolean checkPlayerPermission(TanPlayer player, TerritoryPermission rolePermission);
}
