package org.leralix.tan.api.external.papi.entries;

import org.leralix.tan.dataclass.ITanPlayer;

public class PlayerKingdomBalance extends AbstractPlayerKingdomPapiEntry {

    public PlayerKingdomBalance() {
        super("player_kingdom_balance");
    }

    @Override
    protected String getDataForKingdomPlayer(ITanPlayer tanPlayer) {
        return Double.toString(tanPlayer.getKingdom().getBalance());
    }
}
