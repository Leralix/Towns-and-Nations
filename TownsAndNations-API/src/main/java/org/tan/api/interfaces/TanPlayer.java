package org.tan.api.interfaces;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface TanPlayer {

    /**
     * Get the player name stored in the configuration.
     * It is used to identify the player in menus and messages.
     * @return The name stored in the configuration.
     */
    String getNameStored();

    /**
     * Set the player name stored in the configuration.
     * @param name The new name of the player.
     */
    void setNameStored(String name);

    /**
     * Get the player UUID.
     * @return The UUID of the player.
     */
    UUID getUUID();

    /**
     * @return True if the player is part of a town, false otherwise.
     */
    boolean hasTown();

    /**
     * Get the town the player is part of.
     * @return The town the player is part of or {@link Optional#empty()} if the player is not part of a town.
     */
    Optional<TanTown> getTown();

    /**
     * @return True if the player is part of a region, false otherwise.
     */
    boolean hasRegion();

    /**
     * Get the region the player is part of.
     * @return The region the player is part of or {@link Optional#empty()} if the player is not part of a region.
     */
    TanRegion getRegion();

    /**
     * @return A collection of all the properties owned by the player.
     */
    Collection<TanProperty> getPropertiesOwned();

    /**
     * @return A collection of all the properties rented by the player.
     */
    Collection<TanProperty> getPropertiesRented();

    /**
     * @return A collection of all the properties owned by the player for sale.
     */
    Collection<TanProperty> getPropertiesForSale();
}
