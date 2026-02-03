package org.tan.api.interfaces;

import org.tan.api.interfaces.buildings.TanProperty;
import org.tan.api.interfaces.territory.TanRegion;
import org.tan.api.interfaces.territory.TanTown;
import org.tan.api.interfaces.war.TanWar;

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
     * Get the player UUID.
     * @return The UUID of the player.
     */
    UUID getID();

    /**
     * @return True if the player is part of a town, false otherwise.
     */
    boolean hasTown();

    /**
     * Get the town the player is part of.
     * @return The town the player is part of or null if the player is not part of a town.
     */
    TanTown getTown();

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

    /**
     * @return A collection of all the wars the player is participating in.
     */
    Collection<TanWar> getWarsParticipatingIn();
}
