package org.tan.api.getters;

import java.util.Optional;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.tan.api.interfaces.TanClaimedChunk;
import org.tan.api.interfaces.TanTerritory;

public interface TanClaimManager {
  boolean isBlockClaimed(Block block);

  TanClaimedChunk getClaimedChunk(Location location);

  default TanClaimedChunk getClaimedChunk(Block block) {
    return getClaimedChunk(block.getLocation());
  }

  Optional<TanTerritory> getTerritoryOfBlock(Block block);
}
