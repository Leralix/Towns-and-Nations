package org.leralix.tan.api.external.papi.entries;

import org.leralix.tan.dataclass.ITanPlayer;

public class PlayerKingdomName extends AbstractPlayerKingdomPapiEntry {

    public PlayerKingdomName() {
        super("player_kingdom_name");
    }

    @Override
    protected String getDataForKingdomPlayer(ITanPlayer tanPlayer) {
        return tanPlayer.getKingdom().getName();
    }
}
