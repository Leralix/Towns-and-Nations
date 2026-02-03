package org.leralix.tan.events.events;

import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.events.InternalEvent;
import org.tan.api.events.PlayerJoinTownEvent;
import org.tan.api.interfaces.TanPlayer;
import org.tan.api.interfaces.territory.TanTown;

public class PlayerJoinTownAcceptedInternalEvent extends InternalEvent implements PlayerJoinTownEvent {

    private final ITanPlayer player;

    private final TanTown town;

    public PlayerJoinTownAcceptedInternalEvent(ITanPlayer player, TanTown town) {
        this.player = player;
        this.town = town;
    }


    @Override
    public TanPlayer getPlayer() {
        return player;
    }

    @Override
    public TanTown getTown() {
        return town;
    }
}
