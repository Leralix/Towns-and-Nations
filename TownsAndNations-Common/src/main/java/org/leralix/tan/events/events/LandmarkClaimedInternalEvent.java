package org.leralix.tan.events.events;

import org.leralix.tan.events.InternalEvent;
import org.tan.api.events.LandmarkClaimedEvent;
import org.tan.api.interfaces.TanLandmark;
import org.tan.api.interfaces.TanTerritory;

public class LandmarkClaimedInternalEvent extends InternalEvent implements LandmarkClaimedEvent {

    private final TanLandmark claimedLandmark;
    private final TanTerritory newOwner;

    public LandmarkClaimedInternalEvent(TanLandmark landmark, TanTerritory newOwner) {
        this.claimedLandmark = landmark;
        this.newOwner = newOwner;
    }

    @Override
    public TanLandmark getLandmark() {
        return claimedLandmark;
    }

    @Override
    public TanTerritory getNewOwner() {
        return newOwner;
    }
}
