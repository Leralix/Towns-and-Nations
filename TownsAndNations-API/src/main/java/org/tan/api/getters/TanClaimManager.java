package org.tan.api.getters;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.tan.api.interfaces.TanClaimedChunk;
import org.tan.api.interfaces.TanTerritory;

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

    TanClaimedChunk getClaimedChunk(Location location);


    default TanClaimedChunk getClaimedChunk(Block block){
        return getClaimedChunk(block.getLocation());
    }

    /**
     * Get the territory own a block.
     * <br>
     * If the block is claimed by a landmark, the territory owning the landmark will be returned.
     * @param block The block to check.
     * @return The territory owning the block, or {@link Optional#empty()} if the block is not claimed.
     */
    Optional<TanTerritory> getTerritoryOfBlock(Block block);

}
