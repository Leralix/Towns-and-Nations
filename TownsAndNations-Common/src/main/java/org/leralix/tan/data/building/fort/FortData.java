package org.leralix.tan.data.building.fort;

import org.leralix.lib.position.Vector3D;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.utils.gameplay.TerritoryUtil;

public class FortData extends Fort{

    private final String id;

    private final Vector3D position;

    private String name;

    private String ownerID;

    private String occupierID;

    public FortData(String id, Vector3D position, String name, TerritoryData owningTerritory){
        this.id = id;
        this.position = position;
        this.name = name;
        this.ownerID = owningTerritory.getID();
        this.occupierID = owningTerritory.getID();
        spawnFlag();
    }


    @Override
    public String getID() {
        return id;
    }

    @Override
    public Vector3D getPosition() {
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
    public void setOccupierInternal(TerritoryData newOwner) {
        this.occupierID = newOwner.getID();
    }

    @Override
    public void setOwner(TerritoryData newOwner) {
        this.ownerID = newOwner.getID();
    }
}
