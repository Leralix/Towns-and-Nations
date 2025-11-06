package org.tan.api;

import java.util.UUID;
import org.bukkit.entity.Player;
import org.tan.api.interfaces.TanTown;

/** API for interacting with towns. */
public interface TownAPI {

  /**
   * Get a town by its name.
   *
   * @param townName The name of the town.
   * @return The town, or null if not found.
   */
  TanTown getTown(String townName);

  /**
   * Get the town a player is in.
   *
   * @param player The player.
   * @return The town, or null if the player is not in a town.
   */
  TanTown getTown(Player player);

  /**
   * Get the town a player is in.
   *
   * @param playerUUID The player's UUID.
   * @return The town, or null if the player is not in a town.
   */
  TanTown getTown(UUID playerUUID);

  /**
   * Create a new town.
   *
   * @param player The player creating the town.
   * @param townName The name of the new town.
   */
  void createTown(Player player, String townName);

  /**
   * Delete a town.
   *
   * @param townName The name of the town to delete.
   */
  void deleteTown(String townName);
}
