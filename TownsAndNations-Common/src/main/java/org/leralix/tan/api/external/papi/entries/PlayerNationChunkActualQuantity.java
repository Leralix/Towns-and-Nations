package org.leralix.tan.api.external.papi.entries;

import org.leralix.tan.dataclass.ITanPlayer;

public class PlayerNationChunkActualQuantity extends AbstractPlayerNationPapiEntry {

    public PlayerNationChunkActualQuantity() {
        super("player_nation_chunk_actual_quantity");
    }

    @Override
    protected String getDataForNationPlayer(ITanPlayer tanPlayer) {
        return Integer.toString(tanPlayer.getNation().getNumberOfClaimedChunk());
    }
}
