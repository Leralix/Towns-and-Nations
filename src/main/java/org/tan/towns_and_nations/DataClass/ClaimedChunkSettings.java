package org.tan.towns_and_nations.DataClass;

public class ClaimedChunkSettings {
    private boolean openChest;
    private boolean openDoor;
    private boolean breakBlock;
    private boolean placeBlock;

    public ClaimedChunkSettings(){
        this.openChest = false;
        this.openDoor = false;
        this.breakBlock = false;
        this.placeBlock = false;
    }

    public boolean isOpenChest() {
        return openChest;
    }

    public void setOpenChest(boolean openChest) {
        this.openChest = openChest;
    }

    public boolean isOpenDoor() {
        return openDoor;
    }

    public void setOpenDoor(boolean openDoor) {
        this.openDoor = openDoor;
    }

    public boolean isBreakBlock() {
        return breakBlock;
    }

    public void setBreakBlock(boolean breakBlock) {
        this.breakBlock = breakBlock;
    }

    public boolean isPlaceBlock() {
        return placeBlock;
    }

    public void setPlaceBlock(boolean placeBlock) {
        this.placeBlock = placeBlock;
    }


}
