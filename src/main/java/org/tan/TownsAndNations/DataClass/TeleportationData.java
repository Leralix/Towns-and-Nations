package org.tan.TownsAndNations.DataClass;

public class TeleportationData {

    private final TownData townData;
    private Boolean isCancelled;

    public TeleportationData(TownData townData){
        this.townData = townData;
        this.isCancelled = false;
    }

    public TownData getTownData() {
        return townData;
    }

    public Boolean isCancelled() {
        return isCancelled;
    }
    public void setCancelled(Boolean bool){
        this.isCancelled = bool;
    }


}
