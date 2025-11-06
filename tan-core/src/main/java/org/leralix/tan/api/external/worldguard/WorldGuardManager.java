package org.leralix.tan.api.external.worldguard;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.leralix.tan.enums.permissions.ChunkPermissionType;

public class WorldGuardManager {

  private WorldGuardImplementation implementation;

  private static WorldGuardManager instance;

  private WorldGuardManager() {}

  public static WorldGuardManager getInstance() {
    if (instance == null) {
      instance = new WorldGuardManager();
    }
    return instance;
  }

  public void register() {
    implementation = new WorldGuardImplementation();
  }

  public boolean isEnabled() {
    return implementation != null;
  }

  public boolean isHandledByWorldGuard(Location location) {
    if (!isEnabled()) return false;
    return implementation.isHandledByWorldGuard(location);
  }

  public boolean isActionAllowed(Player player, Location location, ChunkPermissionType actionType) {
    if (!isEnabled()) return false;
    return implementation.isActionAllowed(player, location, actionType);
  }
}
