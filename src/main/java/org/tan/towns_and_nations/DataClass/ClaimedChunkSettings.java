package org.tan.towns_and_nations.DataClass;

import org.tan.towns_and_nations.enums.TownChunkPermission;

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
        this.chestAuth = nextAuth(this.chestAuth);
    }

    public void nextDoorAuth() {
        this.doorAuth = nextAuth(this.doorAuth);
    }

    public void nextBreakAuth() {
        this.breakAuth = nextAuth(this.breakAuth);
    }

    public void nextPlaceAuth() {
        this.placeAuth = nextAuth(this.placeAuth);
    }

    public TownChunkPermission nextAuth(TownChunkPermission auth) {
        switch (auth) {
            case TOWN -> {
                return TownChunkPermission.ALLIANCE;
            }
            case ALLIANCE -> {
                return TownChunkPermission.FOREIGN;
            }
            case FOREIGN -> {
                return TownChunkPermission.TOWN;
            }
        }
        return null;
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
