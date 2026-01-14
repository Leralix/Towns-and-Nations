package org.leralix.tan.api.external.papi.entries;

import org.leralix.tan.dataclass.ITanPlayer;

public class PlayerNationName extends AbstractPlayerNationPapiEntry {

    public PlayerNationName() {
        super("player_nation_name");
    }

    @Override
    protected String getDataForNationPlayer(ITanPlayer tanPlayer) {
        return tanPlayer.getNation().getName();
    }
}
