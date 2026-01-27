package org.leralix.tan.events.events;

import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.events.InternalEvent;
import org.tan.api.events.PlayerJoinRequestEvent;
import org.tan.api.interfaces.TanPlayer;
import org.tan.api.interfaces.TanTown;

public class PlayerJoinTownRequestInternalEvent extends InternalEvent implements PlayerJoinRequestEvent {

    private final ITanPlayer tanPlayer;

    private final TanTown town;

    public PlayerJoinTownRequestInternalEvent(ITanPlayer player, TanTown town) {
        this.tanPlayer = player;
        this.town = town;
    }


    @Override
    public TanPlayer getPlayer() {
        return tanPlayer;
    }

    @Override
    public TanTown getTown() {
        return town;
    }
}
