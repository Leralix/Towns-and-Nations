package org.leralix.tan.data.chunk;

import org.leralix.tan.data.DbManager;
import org.leralix.tan.data.territory.Town;

public class TownChunkDatabase extends TerritoryChunkDatabase implements TownChunk{

    private TownClaimedChunk data;

    public TownChunkDatabase(TownClaimedChunk townChunk, DbManager<ChunkData> databaseManager) {
        super(townChunk, databaseManager);
        this.data = townChunk;
    }

    @Override
    public Town getTown() {
        return data.getTown();
    }

    @Override
    public void setData(ChunkData data) {
        this.data = (TownClaimedChunk) data;
    }
}
