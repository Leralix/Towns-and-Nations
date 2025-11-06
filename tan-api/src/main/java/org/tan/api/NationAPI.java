package org.tan.api;

import java.util.UUID;
import org.bukkit.entity.Player;
import org.tan.api.interfaces.TanNation;

/** API for interacting with nations. */
public interface NationAPI {

  /**
   * Get a nation by its name.
   *
   * @param nationName The name of the nation.
   * @return The nation, or null if not found.
   */
  TanNation getNation(String nationName);

  /**
   * Get the nation a player is in.
   *
   * @param player The player.
   * @return The nation, or null if the player is not in a nation.
   */
  TanNation getNation(Player player);

  /**
   * Get the nation a player is in.
   *
   * @param playerUUID The player's UUID.
   * @return The nation, or null if the player is not in a nation.
   */
  TanNation getNation(UUID playerUUID);

  /**
   * Create a new nation.
   *
   * @param player The player creating the nation.
   * @param nationName The name of the new nation.
   */
  void createNation(Player player, String nationName);

  /**
   * Delete a nation.
   *
   * @param nationName The name of the nation to delete.
   */
  void deleteNation(String nationName);
}
