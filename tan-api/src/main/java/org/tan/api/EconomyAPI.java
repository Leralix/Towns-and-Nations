package org.tan.api;

import java.util.UUID;
import org.bukkit.entity.Player;

/** API for interacting with the economy. */
public interface EconomyAPI {

  /**
   * Get the balance of a player.
   *
   * @param player The player.
   * @return The player's balance.
   */
  double getBalance(Player player);

  /**
   * Get the balance of a player.
   *
   * @param playerUUID The player's UUID.
   * @return The player's balance.
   */
  double getBalance(UUID playerUUID);

  /**
   * Set the balance of a player.
   *
   * @param player The player.
   * @param amount The new balance.
   */
  void setBalance(Player player, double amount);

  /**
   * Set the balance of a player.
   *
   * @param playerUUID The player's UUID.
   * @param amount The new balance.
   */
  void setBalance(UUID playerUUID, double amount);

  /**
   * Add money to a player's balance.
   *
   * @param player The player.
   * @param amount The amount to add.
   */
  void addToBalance(Player player, double amount);

  /**
   * Add money to a player's balance.
   *
   * @param playerUUID The player's UUID.
   * @param amount The amount to add.
   */
  void addToBalance(UUID playerUUID, double amount);

  /**
   * Remove money from a player's balance.
   *
   * @param player The player.
   * @param amount The amount to remove.
   */
  void removeFromBalance(Player player, double amount);

  /**
   * Remove money from a player's balance.
   *
   * @param playerUUID The player's UUID.
   * @param amount The amount to remove.
   */
  void removeFromBalance(UUID playerUUID, double amount);
}
