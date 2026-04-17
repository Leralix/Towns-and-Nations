package org.leralix.tan.api.external.papi.entries;

import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.storage.stored.NationStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionStorage;
import org.leralix.tan.storage.stored.TownStorage;

public class PlayerNationRankName extends AbstractPlayerNationPapiEntry {

    public PlayerNationRankName(
            PlayerDataStorage playerDataStorage,
            TownStorage townStorage,
            RegionStorage regionDataStorage,
            NationStorage nationDataStorage)
    {
        super("player_nation_rank_name", playerDataStorage, townStorage, regionDataStorage, nationDataStorage);
    }

    @Override
    protected String getDataForNationPlayer(ITanPlayer tanPlayer) {
        return tanPlayer.getNationRank().getName();
    }
}
