package org.leralix.tan.data.chunk;

import org.leralix.tan.data.building.landmark.Landmark;

public interface LandmarkChunk extends IClaimedChunk {
    Landmark getLandMark();
}
