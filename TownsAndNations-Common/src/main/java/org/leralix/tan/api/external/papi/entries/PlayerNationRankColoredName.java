package org.leralix.tan.api.external.papi.entries;

import org.leralix.tan.data.player.ITanPlayer;

public class PlayerNationRankColoredName extends AbstractPlayerNationPapiEntry {

    public PlayerNationRankColoredName() {
        super("player_nation_rank_colored_name");
    }

    @Override
    protected String getDataForNationPlayer(ITanPlayer tanPlayer) {
        return tanPlayer.getNationRank().getColoredName();
    }
}
