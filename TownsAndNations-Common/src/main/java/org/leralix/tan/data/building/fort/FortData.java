package org.leralix.tan.data.building.fort;

import org.leralix.lib.position.Vector3D;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.utils.gameplay.TerritoryUtil;

public class FortData extends Fort{

    private final String id;

    private final Vector3D position;

    private String name;

    private String ownerID;

    private String occupierID;

    public FortData(String id, Vector3D position, String name, Territory owningTerritory){
        this.id = id;
        this.position = position;
        this.name = name;
        this.ownerID = owningTerritory.getID();
        this.occupierID = owningTerritory.getID();
    }

    public FortData(
            String id,
            Vector3D position,
            String name,
            String owningTerritoryID,
            String occupierTerritoryID
    ){
        this.id = id;
        this.position = position;
        this.name = name;
        this.ownerID = owningTerritoryID;
        this.occupierID = occupierTerritoryID;
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
    public Territory getOwner() {
        return TerritoryUtil.getTerritory(ownerID);
    }

    @Override
    public Territory getOccupier() {
        return TerritoryUtil.getTerritory(occupierID);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setOccupierInternal(Territory newOwner) {
        this.occupierID = newOwner.getID();
    }

    @Override
    public void setOwner(Territory newOwner) {
        this.ownerID = newOwner.getID();
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
