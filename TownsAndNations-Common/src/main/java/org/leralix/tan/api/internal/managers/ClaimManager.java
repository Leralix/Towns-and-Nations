package org.leralix.tan.api.internal.managers;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.chunk.IClaimedChunk;
import org.leralix.tan.data.chunk.LandmarkClaimedChunk;
import org.leralix.tan.data.chunk.TerritoryChunkData;
import org.leralix.tan.data.chunk.WildernessChunkData;
import org.leralix.tan.storage.stored.ClaimStorage;
import org.tan.api.getters.TanClaimManager;
import org.tan.api.interfaces.chunk.TanClaimedChunk;
import org.tan.api.interfaces.territory.TanTerritory;

import java.util.Optional;

public class ClaimManager implements TanClaimManager {

    private final ClaimStorage newClaimedChunkStorage;

    private static ClaimManager instance;

    private ClaimManager() {
        newClaimedChunkStorage = TownsAndNations.getPlugin().getClaimStorage();
    }

    public static ClaimManager getInstance() {
        if(instance == null) {
            instance = new ClaimManager();
        }
        return instance;
    }

    @Override
    public boolean isBlockClaimed(Block block) {
        IClaimedChunk claimedChunk = newClaimedChunkStorage.get(block.getChunk());
        return claimedChunk.isClaimed();
    }

    @Override
    public TanClaimedChunk getClaimedChunk(Location location) {
        return newClaimedChunkStorage.get(location.getChunk());
    }

    @Override
    public Optional<TanTerritory> getTerritoryOfBlock(Block block) {
        return getTerritoryOfChunk(block.getChunk());
    }

    public Optional<TanTerritory> getTerritoryOfChunk(Chunk chunk) {
        IClaimedChunk claimedChunk = newClaimedChunkStorage.get(chunk);

        return switch (claimedChunk){
            case WildernessChunkData ignored -> Optional.empty();
            case LandmarkClaimedChunk landmarkClaimedChunk -> Optional.ofNullable(landmarkClaimedChunk.getLandMark().getOwner());
            case TerritoryChunkData territoryChunk -> Optional.ofNullable(territoryChunk.getOwner());
            default -> throw new IllegalStateException("Unexpected chunk type : " + claimedChunk);
        };
    }
}
