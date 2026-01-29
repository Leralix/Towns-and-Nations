package org.leralix.tan.api.external.papi.entries;

import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.storage.stored.PlayerDataStorage;

public class PlayerNationRankColoredName extends AbstractPlayerNationPapiEntry {

    public PlayerNationRankColoredName(PlayerDataStorage playerDataStorage) {
        super("player_nation_rank_colored_name", playerDataStorage);
    }

    @Override
    protected String getDataForNationPlayer(ITanPlayer tanPlayer) {
        return tanPlayer.getNationRank().getColoredName();
    }
}
