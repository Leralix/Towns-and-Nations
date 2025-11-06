package org.tan.api.interfaces;

import java.util.Optional;
import java.util.UUID;
import org.bukkit.entity.Player;

/** Represents a player. */
public interface TanPlayer {

  /**
   * Get the player's UUID.
   *
   * @return The player's UUID.
   */
  UUID getUUID();

  /**
   * Get the player's name.
   *
   * @return The player's name.
   */
  String getName();

  /**
   * Get the player's town.
   *
   * @return The player's town, or null if they are not in a town.
   */
  Optional<TanTown> getTown();

  /**
   * Get the player's Bukkit Player object.
   *
   * @return The player's Bukkit Player object, or null if they are not online.
   */
  Player getPlayer();
}
