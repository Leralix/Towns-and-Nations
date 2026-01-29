package org.leralix.tan.api.external.papi.entries;

import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.storage.stored.PlayerDataStorage;

public class PlayerNationName extends AbstractPlayerNationPapiEntry {

    public PlayerNationName(PlayerDataStorage playerDataStorage) {
        super("player_nation_name", playerDataStorage);
    }

    @Override
    protected String getDataForNationPlayer(ITanPlayer tanPlayer) {
        return tanPlayer.getNation().getName();
    }
}
