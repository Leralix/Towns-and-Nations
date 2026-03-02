package org.leralix.tan.data.territory.teleportation;

public class PlannedTeleportation {

    private final TeleportationData teleportationPosition;
    private boolean isCancelled;

    public PlannedTeleportation(TeleportationData teleportationPosition){
        this.teleportationPosition = teleportationPosition;
        this.isCancelled = false;
    }

    public TeleportationData getTeleportationPosition() {
        return teleportationPosition;
    }

    public boolean isCancelled() {
        return isCancelled;
    }
    public void setCancelled(Boolean bool){
        this.isCancelled = bool;
    }


}
