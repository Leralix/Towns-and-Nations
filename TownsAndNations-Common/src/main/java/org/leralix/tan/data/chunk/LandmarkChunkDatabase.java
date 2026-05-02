package org.leralix.tan.data.chunk;

import org.leralix.tan.data.DbManager;
import org.leralix.tan.data.building.landmark.Landmark;

public class LandmarkChunkDatabase extends ChunkDatabase implements LandmarkChunk {

    private LandmarkClaimedChunk data;

    public LandmarkChunkDatabase(LandmarkClaimedChunk data, DbManager<ChunkData> manager) {
        super(data, manager);
        this.data = data;
    }

    @Override
    public void setData(ChunkData data) {
        this.data = (LandmarkClaimedChunk) data;
    }

    @Override
    public Landmark getLandMark() {
        return data.getLandMark();
    }
}
