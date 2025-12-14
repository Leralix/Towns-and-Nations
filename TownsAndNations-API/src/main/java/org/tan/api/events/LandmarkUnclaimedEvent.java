package org.tan.api.events;

import org.tan.api.interfaces.TanLandmark;
import org.tan.api.interfaces.TanTerritory;

public interface LandmarkUnclaimedEvent extends TanEvent {

    /**
     * Get the landmark that has been unclaimed
     * @return The landmark
     */
    TanLandmark getLandmark();

    /**
     * Get the old owner of the landmark
     * @return The old owner
     */
    TanTerritory getOldOwner();

}
