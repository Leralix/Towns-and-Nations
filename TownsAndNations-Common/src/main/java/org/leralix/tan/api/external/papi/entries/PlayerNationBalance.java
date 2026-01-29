package org.leralix.tan.api.external.papi.entries;

import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.storage.stored.PlayerDataStorage;

public class PlayerNationBalance extends AbstractPlayerNationPapiEntry {

    public PlayerNationBalance(PlayerDataStorage playerDataStorage) {
        super("player_nation_balance", playerDataStorage);
    }

    @Override
    protected String getDataForNationPlayer(ITanPlayer tanPlayer) {
        return Double.toString(tanPlayer.getNation().getBalance());
    }
}
