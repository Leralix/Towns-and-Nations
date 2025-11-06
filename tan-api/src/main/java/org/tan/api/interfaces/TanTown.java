package org.tan.api.interfaces;

import java.util.Collection;
import java.util.UUID;
import org.bukkit.entity.Player;

/** Represents a town. */
public interface TanTown extends TanTerritory {

  /**
   * Get the leader of the town.
   *
   * @return The UUID of the leader.
   */
  String getLeader();

  /**
   * Get all the members of the town.
   *
   * @return A collection of the UUIDs of all members.
   */
  Collection<TanPlayer> getMembers();

  /**
   * Check if a player is a member of the town.
   *
   * @param player The player to check.
   * @return True if the player is a member, false otherwise.
   */
  boolean isMember(Player player);

  /**
   * Check if a player is a member of the town.
   *
   * @param playerUUID The UUID of the player to check.
   * @return True if the player is a member, false otherwise.
   */
  boolean isMember(UUID playerUUID);
}
