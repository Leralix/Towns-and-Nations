package org.tan.api;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.tan.api.interfaces.TanClaimedChunk;

/** API for interacting with claims. */
public interface ClaimAPI {

  /**
   * Get the claimed chunk at a specific location.
   *
   * @param location The location.
   * @return The claimed chunk, or null if the chunk is not claimed.
   */
  TanClaimedChunk getClaimedChunk(Location location);

  /**
   * Get the claimed chunk at a specific chunk.
   *
   * @param chunk The chunk.
   * @return The claimed chunk, or null if the chunk is not claimed.
   */
  TanClaimedChunk getClaimedChunk(Chunk chunk);

  /**
   * Check if a chunk is claimed.
   *
   * @param chunk The chunk to check.
   * @return True if the chunk is claimed, false otherwise.
   */
  boolean isClaimed(Chunk chunk);

  /**
   * Check if a location is in a claimed chunk.
   *
   * @param location The location to check.
   * @return True if the location is in a claimed chunk, false otherwise.
   */
  boolean isClaimed(Location location);

  /**
   * Claim a chunk for a town.
   *
   * @param player The player claiming the chunk.
   * @param chunk The chunk to claim.
   */
  void claimChunk(Player player, Chunk chunk);

  /**
   * Unclaim a chunk.
   *
   * @param chunk The chunk to unclaim.
   */
  void unclaimChunk(Chunk chunk);
}
