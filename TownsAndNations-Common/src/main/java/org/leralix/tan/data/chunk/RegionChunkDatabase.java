package org.leralix.tan.data.chunk;

import org.leralix.tan.data.DbManager;
import org.leralix.tan.data.territory.Region;

public class RegionChunkDatabase extends TerritoryChunkDatabase implements RegionChunk{

    private RegionChunk data;

    public RegionChunkDatabase(DbManager<IClaimedChunk> databaseManager, RegionChunk regionChunk) {
        super(databaseManager, regionChunk);
        this.data = regionChunk;
    }

    @Override
    public Region getRegion() {
        return data.getRegion();
    }

    @Override
    public void setData(IClaimedChunk data) {
        this.data = (RegionChunk) data;
    }
}
