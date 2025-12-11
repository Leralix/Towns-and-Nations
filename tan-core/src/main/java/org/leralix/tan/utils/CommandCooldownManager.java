package org.leralix.tan.utils;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Player;

public class CommandCooldownManager {

  private static final CommandCooldownManager INSTANCE = new CommandCooldownManager();

  private final Map<UUID, Map<String, Long>> cooldowns = new ConcurrentHashMap<>();

  private CommandCooldownManager() {}

  public static CommandCooldownManager getInstance() {
    return INSTANCE;
  }

  public void setCooldown(Player player, String commandKey, long seconds) {
    setCooldown(player.getUniqueId(), commandKey, seconds);
  }

  public void setCooldown(UUID playerUUID, String commandKey, long seconds) {
    long expiryTime = System.currentTimeMillis() + (seconds * 1000);
    cooldowns
        .computeIfAbsent(playerUUID, k -> new ConcurrentHashMap<>())
        .put(commandKey, expiryTime);
  }

  public boolean hasCooldown(Player player, String commandKey) {
    return hasCooldown(player.getUniqueId(), commandKey);
  }

  public boolean hasCooldown(UUID playerUUID, String commandKey) {
    Map<String, Long> playerCooldowns = cooldowns.get(playerUUID);
    if (playerCooldowns == null) {
      return false;
    }

    Long expiryTime = playerCooldowns.get(commandKey);
    if (expiryTime == null) {
      return false;
    }

    if (System.currentTimeMillis() >= expiryTime) {
      playerCooldowns.remove(commandKey);
      return false;
    }

    return true;
  }

  public long getRemainingCooldown(Player player, String commandKey) {
    return getRemainingCooldown(player.getUniqueId(), commandKey);
  }

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

  public void clearCooldowns(UUID playerUUID) {
    cooldowns.remove(playerUUID);
  }

  public void clearCooldown(Player player, String commandKey) {
    clearCooldown(player.getUniqueId(), commandKey);
  }

  public void clearCooldown(UUID playerUUID, String commandKey) {
    Map<String, Long> playerCooldowns = cooldowns.get(playerUUID);
    if (playerCooldowns != null) {
      playerCooldowns.remove(commandKey);
    }
  }

  public void cleanupExpiredCooldowns() {
    long now = System.currentTimeMillis();
    cooldowns
        .values()
        .forEach(
            playerCooldowns ->
                playerCooldowns.entrySet().removeIf(entry -> entry.getValue() <= now));

    cooldowns.entrySet().removeIf(entry -> entry.getValue().isEmpty());
  }
}
