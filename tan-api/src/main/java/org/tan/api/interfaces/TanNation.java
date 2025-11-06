package org.tan.api.interfaces;

import java.util.Collection;
import java.util.UUID;

/** Represents a nation. */
public interface TanNation {

  /**
   * Get the name of the nation.
   *
   * @return The name of the nation.
   */
  String getName();

  /**
   * Get the UUID of the nation.
   *
   * @return The UUID of the nation.
   */
  UUID getUUID();

  /**
   * Get the leader of the nation.
   *
   * @return The UUID of the leader.
   */
  UUID getLeader();

  /**
   * Get all the member towns of the nation.
   *
   * @return A collection of the towns in the nation.
   */
  Collection<TanTown> getTowns();

  /**
   * Check if a town is a member of the nation.
   *
   * @param town The town to check.
   * @return True if the town is a member, false otherwise.
   */
  boolean isMember(TanTown town);
}
