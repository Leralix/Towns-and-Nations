package org.leralix.tan.api.internal.managers;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.leralix.tan.data.chunk.ClaimedChunk;
import org.leralix.tan.data.chunk.LandmarkClaimedChunk;
import org.leralix.tan.data.chunk.TerritoryChunk;
import org.leralix.tan.data.chunk.WildernessChunk;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.tan.api.getters.TanClaimManager;
import org.tan.api.interfaces.territory.TanTerritory;
import org.tan.api.interfaces.chunk.TanClaimedChunk;

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
        ClaimedChunk claimedChunk = newClaimedChunkStorage.get(block.getChunk());
        return claimedChunk.isClaimed();
    }

    @Override
    public TanClaimedChunk getClaimedChunk(Location location) {
        return NewClaimedChunkStorage.getInstance().get(location.getChunk());
    }

    @Override
    public Optional<TanTerritory> getTerritoryOfBlock(Block block) {
        ClaimedChunk claimedChunk = newClaimedChunkStorage.get(block.getChunk());

        return switch (claimedChunk){
            case WildernessChunk ignored -> Optional.empty();
            case LandmarkClaimedChunk landmarkClaimedChunk -> Optional.ofNullable(landmarkClaimedChunk.getLandMark().getOwner());
            case TerritoryChunk territoryChunk -> Optional.ofNullable(territoryChunk.getOwner());
            default -> throw new IllegalStateException("Unexpected chunk type : " + claimedChunk);
        };
    }
}
