package org.leralix.tan.api.internal.managers;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.chunk.LandmarkClaimedChunk;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.tan.api.getters.TanClaimManager;
import org.tan.api.interfaces.TanClaimedChunk;
import org.tan.api.interfaces.TanTerritory;

import java.util.Optional;

public class ClaimManager implements TanClaimManager {

    private final NewClaimedChunkStorage newClaimedChunkStorage;

    private static ClaimManager instance;

    private ClaimManager() {
        newClaimedChunkStorage = NewClaimedChunkStorage.getInstance();
    }

    public static ClaimManager getInstance() {
        if(instance == null) {
            instance = new ClaimManager();
        }
        return instance;
    }

    @Override
    public boolean isBlockClaimed(Block block) {
        ClaimedChunk2 claimedChunk = newClaimedChunkStorage.get(block.getChunk());
        return claimedChunk.isClaimed();
    }

    @Override
    public TanClaimedChunk getClaimedChunk(Location location) {
        return NewClaimedChunkStorage.getInstance().get(location.getChunk());
    }

    @Override
    public Optional<TanTerritory> getTerritoryOfBlock(Block block) {
        ClaimedChunk2 claimedChunk = newClaimedChunkStorage.get(block.getChunk());
        if(!claimedChunk.isClaimed()){
            return Optional.empty();
        }
        if(claimedChunk instanceof LandmarkClaimedChunk landmarkClaimedChunk){
            TerritoryData territoryData = landmarkClaimedChunk.getOwner();
            return Optional.ofNullable(territoryData);
        }
        return Optional.ofNullable(claimedChunk.getOwner());
    }
}
