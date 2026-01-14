package org.leralix.tan.api.external.papi.entries;

import org.leralix.tan.dataclass.ITanPlayer;

public class PlayerKingdomRankColoredName extends AbstractPlayerKingdomPapiEntry {

    public PlayerKingdomRankColoredName() {
        super("player_kingdom_rank_colored_name");
    }

    @Override
    protected String getDataForKingdomPlayer(ITanPlayer tanPlayer) {
        return tanPlayer.getKingdomRank().getColoredName();
    }
}
