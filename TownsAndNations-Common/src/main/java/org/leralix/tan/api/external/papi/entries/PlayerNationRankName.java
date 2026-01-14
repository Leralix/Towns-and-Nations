package org.leralix.tan.api.external.papi.entries;

import org.leralix.tan.dataclass.ITanPlayer;

public class PlayerNationRankName extends AbstractPlayerNationPapiEntry {

    public PlayerNationRankName() {
        super("player_nation_rank_name");
    }

    @Override
    protected String getDataForNationPlayer(ITanPlayer tanPlayer) {
        return tanPlayer.getNationRank().getName();
    }
}
