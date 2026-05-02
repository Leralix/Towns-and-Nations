package org.leralix.tan.data.chunk;

import org.leralix.tan.data.DbManager;
import org.leralix.tan.data.territory.Region;

public class RegionChunkDatabase extends TerritoryChunkDatabase implements RegionChunk{

    private RegionClaimedChunk data;

    public RegionChunkDatabase(RegionClaimedChunk regionChunk, DbManager<ChunkData> databaseManager) {
        super(regionChunk, databaseManager);
        this.data = regionChunk;
    }

    @Override
    public Region getRegion() {
        return data.getRegion();
    }

    @Override
    public void setData(ChunkData data) {
        this.data = (RegionClaimedChunk) data;
    }
}
