package org.tan.api.getters;

import org.bukkit.Location;
import org.tan.api.interfaces.TanLandmark;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * The TanLandmarkManager interface provides methods for accessing and managing landmarks within the application.
 * It allows retrieval of landmarks by their names or UUIDs, and provides a collection of all landmarks.
 * Additionally, it supports the creation of new landmarks using coordinates or locations.
 * This interface is essential for handling landmark-related operations and ensuring proper management of landmark data.
 */
public interface TanLandmarkManager {

    /**
     * Get all landmarks
     * @return a {@link Collection} of all {@link TanLandmark}
     */
    Collection<TanLandmark> getLandmarks();

    /**
     * Get a landmark by name
     * <br>
     * It is recommended to use the UUID instead of the name in case
     * duplicate names are allowed in the configuration
     * @param name the name of the landmark
     * @return the {@link TanLandmark} with the specified name, or {@link Optional#empty()} if not found.
     */
    Optional<TanLandmark> getLandmark(String name);

    /**
     * Get a landmark by UUID
     * @param id the UUID of the landmark
     * @return the {@link TanLandmark} with the specified UUID, or {@link Optional#empty()} if not found.
     */
    TanLandmark getLandmark(UUID id);

    /**
     * Create a new landmark
     * <br>
     * The landmark will be saved to the configuration and can be accessible from the API
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @param worldUUID the UUID of the world
     * @return the created {@link TanLandmark}
     */
    TanLandmark createLandmark(double x, double y, double z, UUID worldUUID);

    /**
     * Create a new landmark
     * <br>
     * The landmark will be saved to the configuration and can be accessible from the API
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @param worldName the name of the world
     * @return the created {@link TanLandmark}
     */
    TanLandmark createLandmark(double x, double y, double z, String worldName);

    /**
     * Create a new landmark
     * <br>
     * The landmark will be saved to the configuration and can be accessible from the API
     * @param location the location of the landmark
     * @return the created {@link TanLandmark}
     */
    TanLandmark createLandmark(Location location);

}
