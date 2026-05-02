package org.leralix.tan.data.chunk;

import org.leralix.tan.data.DbManager;

public class NationChunkDatabase extends TerritoryChunkDatabase implements NationChunk{

    private NationClaimedChunk data;

    public NationChunkDatabase(NationClaimedChunk nationData, DbManager<ChunkData> databaseManager) {
        super(nationData, databaseManager);
        this.data = nationData;
    }

    @Override
    public void setData(ChunkData data) {
        this.data = (NationClaimedChunk) data;
    }
}
