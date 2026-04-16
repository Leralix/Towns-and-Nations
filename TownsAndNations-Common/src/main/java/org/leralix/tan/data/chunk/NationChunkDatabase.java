package org.leralix.tan.data.chunk;

import org.leralix.tan.data.DbManager;

public class NationChunkDatabase extends TerritoryChunkDatabase implements NationChunk{

    private NationChunk data;

    public NationChunkDatabase(DbManager<IClaimedChunk> databaseManager, NationChunk nationData) {
        super(databaseManager, nationData);
        this.data = nationData;
    }

    @Override
    public void setData(IClaimedChunk data) {
        this.data = (NationChunk) data;
    }
}
