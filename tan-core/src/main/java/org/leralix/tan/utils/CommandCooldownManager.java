package org.leralix.tan.utils;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Player;

/**
 * Manages command cooldowns to prevent spam and abuse.
 *
 * <p>This class provides a thread-safe way to track when players can execute commands again.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * if (CommandCooldownManager.getInstance().hasCooldown(player, "town_create")) {
 *     long remaining = CommandCooldownManager.getInstance().getRemainingCooldown(player, "town_create");
 *     player.sendMessage("Please wait " + remaining + " seconds before creating another town");
 *     return false;
 * }
 * CommandCooldownManager.getInstance().setCooldown(player, "town_create", 300); // 5 minutes
 * }</pre>
 */
public class CommandCooldownManager {

  private static final CommandCooldownManager INSTANCE = new CommandCooldownManager();

  // Map of player UUID -> command name -> expiry timestamp (milliseconds)
  private final Map<UUID, Map<String, Long>> cooldowns = new ConcurrentHashMap<>();

  private CommandCooldownManager() {}

  public static CommandCooldownManager getInstance() {
    return INSTANCE;
  }

  /**
   * Sets a cooldown for a player on a specific command.
   *
   * @param player The player
   * @param commandKey Unique key for the command (e.g., "town_create", "claim_chunk")
   * @param seconds Duration of cooldown in seconds
   */
  public void setCooldown(Player player, String commandKey, long seconds) {
    setCooldown(player.getUniqueId(), commandKey, seconds);
  }

  /**
   * Sets a cooldown for a player UUID on a specific command.
   *
   * @param playerUUID The player's UUID
   * @param commandKey Unique key for the command
   * @param seconds Duration of cooldown in seconds
   */
  public void setCooldown(UUID playerUUID, String commandKey, long seconds) {
    long expiryTime = System.currentTimeMillis() + (seconds * 1000);
    cooldowns
        .computeIfAbsent(playerUUID, k -> new ConcurrentHashMap<>())
        .put(commandKey, expiryTime);
  }

  /**
   * Checks if a player is on cooldown for a specific command.
   *
   * @param player The player
   * @param commandKey The command key
   * @return true if player is on cooldown, false otherwise
   */
  public boolean hasCooldown(Player player, String commandKey) {
    return hasCooldown(player.getUniqueId(), commandKey);
  }

  /**
   * Checks if a player UUID is on cooldown for a specific command.
   *
   * @param playerUUID The player's UUID
   * @param commandKey The command key
   * @return true if player is on cooldown, false otherwise
   */
  public boolean hasCooldown(UUID playerUUID, String commandKey) {
    Map<String, Long> playerCooldowns = cooldowns.get(playerUUID);
    if (playerCooldowns == null) {
      return false;
    }

    Long expiryTime = playerCooldowns.get(commandKey);
    if (expiryTime == null) {
      return false;
    }

    // Check if cooldown has expired
    if (System.currentTimeMillis() >= expiryTime) {
      playerCooldowns.remove(commandKey);
      return false;
    }

    return true;
  }

  /**
   * Gets the remaining cooldown time in seconds.
   *
   * @param player The player
   * @param commandKey The command key
   * @return Remaining seconds, or 0 if no cooldown
   */
  public long getRemainingCooldown(Player player, String commandKey) {
    return getRemainingCooldown(player.getUniqueId(), commandKey);
  }

  /**
   * Gets the remaining cooldown time in seconds.
   *
   * @param playerUUID The player's UUID
   * @param commandKey The command key
   * @return Remaining seconds, or 0 if no cooldown
   */
  public long getRemainingCooldown(UUID playerUUID, String commandKey) {
    Map<String, Long> playerCooldowns = cooldowns.get(playerUUID);
    if (playerCooldowns == null) {
      return 0;
    }

    Long expiryTime = playerCooldowns.get(commandKey);
    if (expiryTime == null) {
      return 0;
    }

    long remaining = (expiryTime - System.currentTimeMillis()) / 1000;
    return Math.max(0, remaining);
  }

  /**
   * Clears all cooldowns for a player (e.g., when they log out).
   *
   * @param playerUUID The player's UUID
   */
  public void clearCooldowns(UUID playerUUID) {
    cooldowns.remove(playerUUID);
  }

  /**
   * Clears a specific cooldown for a player.
   *
   * @param player The player
   * @param commandKey The command key
   */
  public void clearCooldown(Player player, String commandKey) {
    clearCooldown(player.getUniqueId(), commandKey);
  }

  /**
   * Clears a specific cooldown for a player UUID.
   *
   * @param playerUUID The player's UUID
   * @param commandKey The command key
   */
  public void clearCooldown(UUID playerUUID, String commandKey) {
    Map<String, Long> playerCooldowns = cooldowns.get(playerUUID);
    if (playerCooldowns != null) {
      playerCooldowns.remove(commandKey);
    }
  }

  /** Clears all expired cooldowns (call periodically to free memory). */
  public void cleanupExpiredCooldowns() {
    long now = System.currentTimeMillis();
    cooldowns
        .values()
        .forEach(
            playerCooldowns ->
                playerCooldowns.entrySet().removeIf(entry -> entry.getValue() <= now));

    // Remove players with no cooldowns
    cooldowns.entrySet().removeIf(entry -> entry.getValue().isEmpty());
  }
}
