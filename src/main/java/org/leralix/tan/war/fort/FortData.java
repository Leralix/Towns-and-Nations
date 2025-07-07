package org.leralix.tan.war.fort;

import org.leralix.lib.position.Vector3D;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.utils.TerritoryUtil;

public class FortData extends Fort{

    private final Vector3D position;

    private String name;

    private String ownerID;

    private String occupierID;

    private int captureProgress;

    public FortData(Vector3D position, String name, TerritoryData owningTerritory){
        this.position = position;
        this.name = name;
        this.ownerID = owningTerritory.getID();
        this.occupierID = owningTerritory.getID();
        spawnFlag();
    }


    @Override
    public Vector3D getFlagPosition() {
        return position;
    }

    @Override
    public TerritoryData getOwner() {
        return TerritoryUtil.getTerritory(ownerID);
    }

    @Override
    public TerritoryData getOccupier() {
        return TerritoryUtil.getTerritory(occupierID);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setOccupier(TerritoryData newOwner) {
        this.occupierID = newOwner.getID();
    }

    @Override
    public int getCaptureProgress() {
        return captureProgress;
    }

    @Override
    public void setCaptureProgress(int value) {
        this.captureProgress = value;
    }
}
