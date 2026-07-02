package org.tan.api.getters;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.tan.api.interfaces.TanPlayer;
import org.tan.api.interfaces.chunk.TanClaimedChunk;
import org.tan.api.interfaces.chunk.TanTerritoryChunk;
import org.tan.api.interfaces.territory.TanTerritory;

import java.util.Optional;

/**
 * The TanClaimManager interface provides methods for managing block claims within the application.
 * It allows checking if a block is claimed by a territory or landmark and retrieving the territory that owns a claimed block.
 * This interface is essential for handling claim-related operations and ensuring proper management of block ownership.
 */
public interface TanClaimManager {

    /**
     * Check if a block is claimed by a territory or a landmark.
     * @param block The block to check.
     * @return True if the block is claimed, false otherwise.
     */
    boolean isBlockClaimed(Block block);

    default TanClaimedChunk getClaimedChunk(Location location){
        return getClaimedChunk(location.getChunk());
    }

    default TanClaimedChunk getClaimedChunk(Block block){
        return getClaimedChunk(block.getLocation());
    }

    TanClaimedChunk getClaimedChunk(Chunk chunk);

    /**
     * Get the territory own a block.
     * <br>
     * If the block is claimed by a landmark, the territory owning the landmark will be returned.
     * @param block The block to check.
     * @return The territory owning the block, or {@link Optional#empty()} if the block is not claimed.
     */
    Optional<TanTerritory> getTerritoryOfBlock(Block block);

    /**
     * Get the territory owning a chunk.
     * <br>
     * If the chunk is claimed by a landmark, the territory owning the landmark will be returned.
     * @param chunk The block to check.
     * @return The territory owning the chunk, or {@link Optional#empty()} if the chunk is not claimed.
     */
    Optional<TanTerritory> getTerritoryOfChunk(Chunk chunk);

    /**
     *
     * @return an optional chunk if claimed, {@link Optional#empty()} if the claim failed
     */
    Optional<TanTerritoryChunk> claimChunk(TanClaimedChunk chunk, TanPlayer tanPlayer, TanTerritory territory);

    /**
     *
     * @return an optional chunk if claimed, {@link Optional#empty()} otherwise
     */
    TanTerritoryChunk forceClaim(TanClaimedChunk chunk, TanTerritory territory);
}
