package org.leralix.tan.api.external.papi.entries;

import org.leralix.tan.dataclass.ITanPlayer;

public class PlayerNationBalance extends AbstractPlayerNationPapiEntry {

    public PlayerNationBalance() {
        super("player_nation_balance");
    }

    @Override
    protected String getDataForNationPlayer(ITanPlayer tanPlayer) {
        return Double.toString(tanPlayer.getNation().getBalance());
    }
}
