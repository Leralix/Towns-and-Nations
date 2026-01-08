package org.leralix.tan.api.external.papi.entries;

import org.leralix.tan.dataclass.ITanPlayer;

public class PlayerKingdomRankName extends AbstractPlayerKingdomPapiEntry {

    public PlayerKingdomRankName() {
        super("player_kingdom_rank_name");
    }

    @Override
    protected String getDataForKingdomPlayer(ITanPlayer tanPlayer) {
        return tanPlayer.getKingdomRank().getName();
    }
}
