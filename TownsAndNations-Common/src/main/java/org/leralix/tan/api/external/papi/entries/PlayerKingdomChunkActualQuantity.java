package org.leralix.tan.api.external.papi.entries;

import org.leralix.tan.dataclass.ITanPlayer;

public class PlayerKingdomChunkActualQuantity extends AbstractPlayerKingdomPapiEntry {

    public PlayerKingdomChunkActualQuantity() {
        super("player_kingdom_chunk_actual_quantity");
    }

    @Override
    protected String getDataForKingdomPlayer(ITanPlayer tanPlayer) {
        return Integer.toString(tanPlayer.getKingdom().getNumberOfClaimedChunk());
    }
}
