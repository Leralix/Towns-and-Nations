package org.tan.TownsAndNations.DataClass;

import org.tan.TownsAndNations.enums.TownChunkPermission;

public class ClaimedChunkSettings {
    private int NumberOfClaimedChunk;
    private TownChunkPermission chestAuth;
    private TownChunkPermission doorAuth;
    private TownChunkPermission breakAuth;
    private TownChunkPermission placeAuth;

    public ClaimedChunkSettings(){
        this.NumberOfClaimedChunk = 0;
        this.chestAuth = TownChunkPermission.TOWN;
        this.doorAuth = TownChunkPermission.TOWN;
        this.breakAuth = TownChunkPermission.TOWN;
        this.placeAuth = TownChunkPermission.TOWN;
    }

    public TownChunkPermission getChestAuth() {
        return this.chestAuth;
    }
    public TownChunkPermission getDoorAuth() {
        return this.doorAuth;
    }
    public TownChunkPermission getBreakAuth() {
        return this.breakAuth;
    }
    public TownChunkPermission getPlaceAuth() {
        return this.placeAuth;
    }

    public void nextChestAuth() {
        this.chestAuth = this.chestAuth.getNext();
    }

    public void nextDoorAuth() {
        this.doorAuth = this.doorAuth.getNext();
    }

    public void nextBreakAuth() {
        this.breakAuth = this.breakAuth.getNext();
    }

    public void nextPlaceAuth() {
        this.placeAuth = this.placeAuth.getNext();
    }

    public int getNumberOfClaimedChunk() {
        return this.NumberOfClaimedChunk;
    }
    public void incrementNumberOfClaimedChunk() {
        this.NumberOfClaimedChunk = this.NumberOfClaimedChunk + 1;
    }
    public void decreaseNumberOfClaimedChunk() {
        this.NumberOfClaimedChunk = this.NumberOfClaimedChunk - 1;
    }

}
