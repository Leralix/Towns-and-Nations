package org.leralix.tan.events.events;

import org.leralix.tan.events.InternalEvent;
import org.tan.api.events.LandmarkUnclaimedEvent;
import org.tan.api.interfaces.buildings.TanLandmark;
import org.tan.api.interfaces.territory.TanTerritory;

public class LandmarkUnclaimedInternalEvent extends InternalEvent implements LandmarkUnclaimedEvent {

    private final TanLandmark unclaimedLandmark;
    private final TanTerritory oldOwner;

    public LandmarkUnclaimedInternalEvent(TanLandmark landmark, TanTerritory oldOwner) {
        this.unclaimedLandmark = landmark;
        this.oldOwner = oldOwner;
    }

    @Override
    public TanLandmark getLandmark() {
        return unclaimedLandmark;
    }

    @Override
    public TanTerritory getOldOwner() {
        return oldOwner;
    }
}
