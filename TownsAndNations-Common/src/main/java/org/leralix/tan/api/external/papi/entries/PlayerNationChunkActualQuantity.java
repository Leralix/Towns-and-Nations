package org.leralix.tan.api.external.papi.entries;

import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.storage.stored.PlayerDataStorage;

public class PlayerNationChunkActualQuantity extends AbstractPlayerNationPapiEntry {

    public PlayerNationChunkActualQuantity(PlayerDataStorage playerDataStorage) {
        super("player_nation_chunk_actual_quantity", playerDataStorage);
    }

    @Override
    protected String getDataForNationPlayer(ITanPlayer tanPlayer) {
        return Integer.toString(tanPlayer.getNation().getNumberOfClaimedChunk());
    }
}
