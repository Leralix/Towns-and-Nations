package org.tan.api.events;

import org.tan.api.interfaces.buildings.TanLandmark;
import org.tan.api.interfaces.territory.TanTerritory;

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
