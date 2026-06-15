package org.leralix.tan.utils.territory;

import org.leralix.tan.data.chunk.WildernessChunk;

import java.util.Set;

public class WildernessPolygon {

    Set<WildernessChunk> wildernessChunkSet;

    public WildernessPolygon(Set<WildernessChunk> wildernessChunkSet){
        this.wildernessChunkSet = wildernessChunkSet;
    }

    public int size() {
        return wildernessChunkSet.size();
    }

    public Set<WildernessChunk> getChunks() {
        return wildernessChunkSet;
    }
}
