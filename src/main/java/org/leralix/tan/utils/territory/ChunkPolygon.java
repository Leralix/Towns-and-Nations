package org.leralix.tan.utils.territory;

import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.storage.stored.FortStorage;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.war.fort.Fort;

import java.util.Set;

public class ChunkPolygon {

    private final TerritoryData territoryOwner;
    private final Set<ClaimedChunk2> chunksInPolygon;

    public ChunkPolygon(TerritoryData territoryData, Set<ClaimedChunk2> chunksInPolygon) {
        this.territoryOwner = territoryData;
        this.chunksInPolygon = chunksInPolygon;
    }

    public Set<ClaimedChunk2> getChunksInPolygon() {
        return chunksInPolygon;
    }

    public boolean isSupplied(){
        for(ClaimedChunk2 claimedChunk2 : chunksInPolygon){
            for(Fort fort : FortStorage.getInstance().getOwnedFort(territoryOwner)){
                if(claimedChunk2.containsPosition(fort.getFlagPosition())){
                    return true;
                }
            }
        }
        return false;
    }

    public void unclaimAll(){
        for(ClaimedChunk2 claimedChunk2 : chunksInPolygon){
            NewClaimedChunkStorage.getInstance().unclaimChunk(claimedChunk2);
        }
    }

    public boolean contains(ClaimedChunk2 claimedChunk) {
        for(ClaimedChunk2 chunkInPolygon : chunksInPolygon){
            if(claimedChunk.equals(chunkInPolygon)){
                return true;
            }
        }
        return false;
    }
}
