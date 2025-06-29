package org.leralix.tan.events.events;

import org.leralix.tan.api.wrappers.RegionDataWrapper;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.events.InternalEvent;
import org.tan.api.events.RegionDeletedEvent;
import org.tan.api.interfaces.TanRegion;

public class RegionDeletednternalEvent extends InternalEvent implements RegionDeletedEvent {

    private final RegionData region;

    public RegionDeletednternalEvent(RegionData region) {
        this.region = region;
    }
    
    @Override
    public TanRegion getRegion() {
        return RegionDataWrapper.of(region);
    }
}
