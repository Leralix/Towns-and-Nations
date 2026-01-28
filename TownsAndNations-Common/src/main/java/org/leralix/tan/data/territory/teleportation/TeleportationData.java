package org.leralix.tan.data.territory.teleportation;

public class TeleportationData {

    private final TeleportationPosition teleportationPosition;
    private boolean isCancelled;

    public TeleportationData(TeleportationPosition teleportationPosition){
        this.teleportationPosition = teleportationPosition;
        this.isCancelled = false;
    }

    public TeleportationPosition getTeleportationPosition() {
        return teleportationPosition;
    }

    public boolean isCancelled() {
        return isCancelled;
    }
    public void setCancelled(Boolean bool){
        this.isCancelled = bool;
    }


}
