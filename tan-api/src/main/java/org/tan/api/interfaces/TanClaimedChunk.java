package org.tan.api.interfaces;

import org.bukkit.Chunk;

/** Represents a claimed chunk. */
public interface TanClaimedChunk {

  /**
   * Get the chunk.
   *
   * @return The chunk.
   */
  Chunk getChunk();

  /**
   * Get the owner of the chunk.
   *
   * @return The town that owns the chunk.
   */
  TanTown getOwner();

  /**
   * Check if the chunk is a landmark.
   *
   * @return True if the chunk is a landmark, false otherwise.
   */
  boolean isLandmark();

  /**
   * Get the owner of the chunk.
   *
   * @return The territory that owns the chunk.
   */
  TanTerritory getTerritoryOwner();
}
