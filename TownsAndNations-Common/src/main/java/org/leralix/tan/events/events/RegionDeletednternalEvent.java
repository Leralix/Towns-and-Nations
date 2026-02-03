package org.leralix.tan.events.events;

import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.RegionData;
import org.leralix.tan.events.InternalEvent;
import org.tan.api.events.RegionDeletedEvent;
import org.tan.api.interfaces.TanPlayer;
import org.tan.api.interfaces.territory.TanRegion;

public class RegionDeletednternalEvent extends InternalEvent implements RegionDeletedEvent {

    private final TanRegion region;
    private final TanPlayer player;


    public RegionDeletednternalEvent(RegionData region, ITanPlayer player) {
        this.region = region;
        this.player = player;
    }
    
    @Override
    public TanRegion getRegion() {
        return region;
    }

    @Override
    public TanPlayer getExecutor() {
        return player;
    }
}
