package org.tan.api.interfaces;

import org.leralix.lib.position.Vector2D;

import java.util.Collection;
import java.util.Optional;

public interface TanTown extends TanTerritory {

    /**
     * @return a {@link Collection} of {@link TanProperty} in the town
     */
    Collection<TanProperty> getProperties();

    /**
     * @return a {@link Collection} of {@link TanLandmark} owned by the town
     */
    Collection<TanLandmark> getLandmarksOwned();

    /**
     * @return an optional 2D vector representing the capital chunk
     */
    Optional<Vector2D> getCapitalLocation();
}
