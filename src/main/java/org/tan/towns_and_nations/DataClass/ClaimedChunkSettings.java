package org.tan.towns_and_nations.DataClass;

public class ClaimedChunkSettings {
    private int NumberOfClaimedChunk;
    private boolean openChest;
    private boolean openDoor;
    private boolean breakBlock;
    private boolean placeBlock;

    public ClaimedChunkSettings(){
        this.NumberOfClaimedChunk = 0;
        this.openChest = false;
        this.openDoor = false;
        this.breakBlock = false;
        this.placeBlock = false;
    }

    public boolean isOpenChest() {
        return this.openChest;
    }

    public void setOpenChest(boolean openChest) {
        this.openChest = openChest;
    }

    public boolean isOpenDoor() {
        return this.openDoor;
    }

    public void setOpenDoor(boolean openDoor) {
        this.openDoor = openDoor;
    }

    public boolean isBreakBlock() {
        return this.breakBlock;
    }

    public void setBreakBlock(boolean breakBlock) {
        this.breakBlock = breakBlock;
    }

    public boolean isPlaceBlock() {
        return this.placeBlock;
    }

    public void setPlaceBlock(boolean placeBlock) {
        this.placeBlock = placeBlock;
    }


    public int getNumberOfClaimedChunk() {
        return this.NumberOfClaimedChunk;
    }

    public void setNumberOfClaimedChunk(int numberOfClaimedChunk) {
        this.NumberOfClaimedChunk = numberOfClaimedChunk;
    }

    public void incrementNumberOfClaimedChunk() {
        this.NumberOfClaimedChunk = this.NumberOfClaimedChunk + 1;
    }
}
