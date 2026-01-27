package org.leralix.tan.utils.territory;

import org.leralix.lib.position.Vector2D;
import org.leralix.tan.dataclass.chunk.ClaimedChunk;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.storage.stored.FortStorage;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.war.fort.Fort;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ChunkPolygon {

    private final TerritoryData territoryOwner;
    private final Set<ClaimedChunk> chunksInPolygon;

    public ChunkPolygon(TerritoryData territoryData, Set<ClaimedChunk> chunksInPolygon) {
        this.territoryOwner = territoryData;
        this.chunksInPolygon = chunksInPolygon;
    }

    public Set<ClaimedChunk> getChunksInPolygon() {
        return chunksInPolygon;
    }

    public boolean isSupplied(){

        Optional<Vector2D> capitalChunk = resolveCapitalChunk(territoryOwner);


        for(ClaimedChunk claimedChunk : chunksInPolygon){
            for(Fort fort : FortStorage.getInstance().getOwnedFort(territoryOwner)){
                if(claimedChunk.containsPosition(fort.getPosition())){
                    return true;
                }
            }
            if(capitalChunk.isPresent() && claimedChunk.getVector2D().equals(capitalChunk.get())){
                return true;
            }
        }
        return false;
    }

    private static Optional<Vector2D> resolveCapitalChunk(TerritoryData territoryOwner) {
        if (territoryOwner instanceof TownData townData) {
            return townData.getCapitalLocation();
        }

        Set<String> visited = new HashSet<>();
        TerritoryData current = territoryOwner;
        while (current != null && visited.add(current.getID())) {
            TerritoryData capital = current.getCapital();
            if (capital == null) {
                return Optional.empty();
            }
            if (capital instanceof TownData capitalTown) {
                return capitalTown.getCapitalLocation();
            }
            current = capital;
        }
        return Optional.empty();
    }

    public void unclaimAll(){
        for(ClaimedChunk claimedChunk : chunksInPolygon){
            NewClaimedChunkStorage.getInstance().unclaimChunk(claimedChunk);
        }
    }

    public boolean contains(ClaimedChunk claimedChunk) {
        for(ClaimedChunk chunkInPolygon : chunksInPolygon){
            if(claimedChunk.equals(chunkInPolygon)){
                return true;
            }
        }
        return false;
    }
}
