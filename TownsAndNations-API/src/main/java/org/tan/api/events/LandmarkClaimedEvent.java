package org.tan.api.events;

import org.tan.api.interfaces.TanLandmark;
import org.tan.api.interfaces.TanTerritory;

public interface LandmarkClaimedEvent extends TanEvent {

    /**
     * Get the landmark that has been claimed
     * @return The landmark
     */
    TanLandmark getLandmark();

    /**
     * Get the new owner of the landmark
     * @return The new owner
     */
    TanTerritory getNewOwner();

}
