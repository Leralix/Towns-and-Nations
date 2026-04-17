package org.leralix.tan.data.chunk;

import org.leralix.tan.data.DbManager;
import org.leralix.tan.data.building.landmark.Landmark;

public class LandmarkChunkDatabase extends ChunkDatabase implements LandmarkChunk {

    private LandmarkChunk data;

    public LandmarkChunkDatabase(DbManager<IClaimedChunk> manager, LandmarkChunk data) {
        super(data, manager);
        this.data = data;
    }

    @Override
    public void setData(IClaimedChunk data) {
        this.data = (LandmarkChunk) data;
    }

    @Override
    public Landmark getLandMark() {
        return data.getLandMark();
    }
}
