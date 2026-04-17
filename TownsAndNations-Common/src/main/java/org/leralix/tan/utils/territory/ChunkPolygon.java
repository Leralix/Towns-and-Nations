package org.leralix.tan.utils.territory;

import org.leralix.lib.position.Vector2D;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.building.fort.Fort;
import org.leralix.tan.data.chunk.IClaimedChunk;
import org.leralix.tan.data.chunk.TerritoryChunk;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.data.territory.Town;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ChunkPolygon {

    private final Territory territoryOwner;
    private final Set<IClaimedChunk> chunksInPolygon;

    public ChunkPolygon(Territory territoryData, Set<IClaimedChunk> chunksInPolygon) {
        this.territoryOwner = territoryData;
        this.chunksInPolygon = chunksInPolygon;
    }

    public Set<IClaimedChunk> getChunksInPolygon() {
        return chunksInPolygon;
    }

    public boolean isSupplied(){

        Optional<Vector2D> capitalChunk = resolveCapitalChunk(territoryOwner);


        for(IClaimedChunk claimedChunk : chunksInPolygon){
            for(Fort fort : TownsAndNations.getPlugin().getFortStorage().getOwnedFort(territoryOwner)){
                if(claimedChunk.containsPosition(fort.getPosition())){
                    return true;
                }
            }
            if(capitalChunk.isPresent() && claimedChunk.getVector2D().equals(capitalChunk.get())){
                return true;
            }
            if(isNextToVassal(claimedChunk)){
                return true;
            }
        }
        return false;
    }

    private boolean isNextToVassal(IClaimedChunk claimedChunk) {

        var vassalIDs = territoryOwner.getVassalsID();
        if(vassalIDs.isEmpty()){
            return false;
        }

        for(IClaimedChunk chunkAround : TownsAndNations.getPlugin().getClaimStorage().getFourAjacentChunks(claimedChunk)){
            if(chunkAround instanceof TerritoryChunk territoryChunk && vassalIDs.contains(territoryChunk.getOwnerID())){
                return true;
            }
        }

        return false;
    }

    private static Optional<Vector2D> resolveCapitalChunk(Territory territoryOwner) {
        if (territoryOwner instanceof Town townData) {
            return townData.getCapitalLocation();
        }

        Set<String> visited = new HashSet<>();
        Territory current = territoryOwner;
        while (current != null && visited.add(current.getID())) {
            Territory capital = current.getCapital();
            if (capital == null) {
                return Optional.empty();
            }
            if (capital instanceof Town capitalTown) {
                return capitalTown.getCapitalLocation();
            }
            current = capital;
        }
        return Optional.empty();
    }

    public void unclaimAll(){
        for(IClaimedChunk claimedChunk : chunksInPolygon){
            TownsAndNations.getPlugin().getClaimStorage().unclaimChunk(claimedChunk);
        }
    }

    public boolean contains(IClaimedChunk claimedChunk) {
        for(IClaimedChunk chunkInPolygon : chunksInPolygon){
            if(claimedChunk.equals(chunkInPolygon)){
                return true;
            }
        }
        return false;
    }
}
