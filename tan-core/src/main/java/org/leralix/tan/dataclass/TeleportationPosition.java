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
    player.teleportAsync(position.getLocation());
  }
}
