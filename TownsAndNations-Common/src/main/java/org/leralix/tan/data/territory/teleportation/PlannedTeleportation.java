package org.leralix.tan.data.territory.teleportation;

import org.bukkit.Location;

public class PlannedTeleportation {

    private final Location teleportationPosition;
    private boolean isCancelled;

    public PlannedTeleportation(Location teleportationPosition) {
        this.teleportationPosition = teleportationPosition;
        this.isCancelled = false;
    }

    public Location getTeleportationPosition() {
        return teleportationPosition;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public void setCancelled(Boolean bool) {
        this.isCancelled = bool;
    }


}
