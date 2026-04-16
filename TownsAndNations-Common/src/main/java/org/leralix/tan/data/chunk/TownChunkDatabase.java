package org.leralix.tan.data.chunk;

import org.leralix.tan.data.DbManager;
import org.leralix.tan.data.territory.Town;

public class TownChunkDatabase extends TerritoryChunkDatabase implements TownChunk{

    private TownChunk data;

    public TownChunkDatabase(DbManager<IClaimedChunk> databaseManager, TownChunk townChunk) {
        super(databaseManager, townChunk);
        this.data = townChunk;
    }

    @Override
    public Town getTown() {
        return data.getTown();
    }

    @Override
    public void setData(IClaimedChunk data) {
        this.data = (TownChunk) data;
    }
}
