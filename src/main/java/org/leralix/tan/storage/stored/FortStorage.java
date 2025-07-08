package org.leralix.tan.storage.stored;

import org.leralix.lib.position.Vector3D;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.war.fort.Fort;

import java.util.List;

public abstract class FortStorage {

    private static FortStorage instance;

    public static void init(FortStorage newInstance) {
        instance = newInstance;
    }

    public static FortStorage getInstance() {
        return instance;
    }

    public abstract List<Fort> getOccupiedFort(TerritoryData territoryData);

    public abstract List<Fort> getOwnedFort(TerritoryData territoryData);

    public abstract List<Fort> getControlledFort(TerritoryData territoryData);

    public abstract List<Fort> getForts();

    public Fort getFort(Fort fort){
        return getFort(fort.getID());
    }

    public abstract Fort getFort(String fortID);

    public abstract Fort register(Vector3D position, TerritoryData owningTerritory);

    public abstract void delete(String fortID);

    public void delete(Fort fort){
        fort.delete();
        delete(fort.getID());
    }
}
