package org.leralix.tan.dataclass;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.leralix.lib.position.Vector3DWithOrientation;

public class TeleportationPosition {

  private final Vector3DWithOrientation position;

  public TeleportationPosition(Location location) {
    position = new Vector3DWithOrientation(location);
  }

  public void teleport(Player player) {
    // Use async teleport for Folia/Paper compatibility
    // This method is thread-safe and works across regions
    player.teleportAsync(position.getLocation());
  }
}
