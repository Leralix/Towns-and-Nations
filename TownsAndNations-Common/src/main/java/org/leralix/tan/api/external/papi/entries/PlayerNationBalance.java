package org.leralix.tan.api.external.papi.entries;

import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.storage.stored.NationStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionStorage;
import org.leralix.tan.storage.stored.TownStorage;

public class PlayerNationBalance extends AbstractPlayerNationPapiEntry {

    public PlayerNationBalance(
            PlayerDataStorage playerDataStorage,
            TownStorage townStorage,
            RegionStorage regionDataStorage,
            NationStorage nationDataStorage)
    {
        super("player_nation_balance", playerDataStorage, townStorage, regionDataStorage, nationDataStorage);
    }

    @Override
    protected String getDataForNationPlayer(ITanPlayer tanPlayer) {
        return Double.toString(tanPlayer.getNation().getBalance());
    }
}
