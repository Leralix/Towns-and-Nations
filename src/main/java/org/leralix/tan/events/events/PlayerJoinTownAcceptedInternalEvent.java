package org.leralix.tan.events.events;

import org.leralix.tan.api.wrappers.TanPlayerWrapper;
import org.leralix.tan.api.wrappers.TownDataWrapper;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.events.InternalEvent;
import org.tan.api.events.PlayerJoinTownEvent;
import org.tan.api.interfaces.TanPlayer;
import org.tan.api.interfaces.TanTown;

public class PlayerJoinTownAcceptedInternalEvent extends InternalEvent implements PlayerJoinTownEvent {

    private final ITanPlayer iPlayer;

    private final TownData townData;

    public PlayerJoinTownAcceptedInternalEvent(ITanPlayer iPlayer, TownData townData) {
        this.iPlayer = iPlayer;
        this.townData = townData;
    }


    @Override
    public TanPlayer getPlayer() {
        return TanPlayerWrapper.of(iPlayer);
    }

    @Override
    public TanTown getTown() {
        return TownDataWrapper.of(townData);
    }
}
