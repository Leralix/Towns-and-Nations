package org.leralix.tan.dataclass;

public class TeleportationData {

    private final TeleportationPosition teleportationPosition;
    private Boolean isCancelled;

    public TeleportationData(TeleportationPosition teleportationPosition){
        this.teleportationPosition = teleportationPosition;
        this.isCancelled = false;
    }

    public TeleportationPosition getTeleportationPosition() {
        return teleportationPosition;
    }

    public Boolean isCancelled() {
        return isCancelled;
    }
    public void setCancelled(Boolean bool){
        this.isCancelled = bool;
    }


}
