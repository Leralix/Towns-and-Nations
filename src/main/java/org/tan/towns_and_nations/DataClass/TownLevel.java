package org.tan.towns_and_nations.DataClass;

public class TownLevel {

    private int TownLevel;
    private int playerUpgrade;
    private int chunkUpgrade;
    private boolean HaveBuySpawn;


    public int getTownLevel() {
        return TownLevel;
    }

    public void setTownLevel(int townLevel) {
        TownLevel = townLevel;
    }

    public int getPlayerUpgrade() {
        return playerUpgrade;
    }

    public void setPlayerUpgrade(int playerUpgrade) {
        this.playerUpgrade = playerUpgrade;
    }

    public int getChunkUpgrade() {
        return chunkUpgrade;
    }

    public void setChunkUpgrade(int chunkUpgrade) {
        this.chunkUpgrade = chunkUpgrade;
    }

    public boolean isHaveBuySpawn() {
        return HaveBuySpawn;
    }

    public void setHaveBuySpawn(boolean haveBuySpawn) {
        HaveBuySpawn = haveBuySpawn;
    }

}
