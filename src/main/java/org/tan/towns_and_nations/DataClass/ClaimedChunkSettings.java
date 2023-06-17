package org.tan.towns_and_nations.DataClass;

public class ClaimedChunkSettings {
    private int NumberOfClaimedChunk;
    private String chestAuth;
    private String doorAuth;
    private String breakAuth;
    private String placeAuth;

    public ClaimedChunkSettings(){
        this.NumberOfClaimedChunk = 0;
        this.chestAuth = "town";
        this.doorAuth = "town";
        this.breakAuth = "town";
        this.placeAuth = "town";
    }

    public String getChestAuth() {
        return this.chestAuth;
    }
    public String getDoorAuth() {
        return this.doorAuth;
    }
    public String getBreakAuth() {
        return this.breakAuth;
    }
    public String getPlaceAuth() {
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

    public String nextAuth(String auth) {
        switch (auth) {
            case "town" -> {
                return "ally";
            }
            case "ally" -> {
                return "foreign";
            }
            case "foreign" -> {
                return "town";
            }
        }
        return null;
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
    public void decreaseNumberOfClaimedChunk() {
        this.NumberOfClaimedChunk = this.NumberOfClaimedChunk - 1;
    }

}
