package org.leralix.tan.storage.stored;

import org.leralix.lib.position.Vector3D;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.war.fort.Fort;

import java.util.ArrayList;
import java.util.List;

public abstract class FortStorage {

    private static FortStorage instance;

    public static void init(FortStorage newInstance) {
        instance = newInstance;
    }

    public static FortStorage getInstance() {
        if (instance == null) {
            throw new IllegalStateException("FortStorage has not been initialized.");
        }
        return instance;
    }

    public abstract List<Fort> getOccupiedFort(TerritoryData territoryData);

    public abstract List<Fort> getOwnedFort(TerritoryData territoryData);

    public abstract List<Fort> getAllControlledFort(TerritoryData territoryData);

    public abstract List<Fort> getForts();

    public Fort getFort(Fort fort){
        return getFort(fort.getID());
    }

    public abstract Fort getFort(String fortID);

    public abstract Fort register(Vector3D position, TerritoryData owningTerritory);

    protected abstract void delete(String fortID);

    public void delete(Fort fort){
        fort.delete();
        delete(fort.getID());
    }

    public abstract void save();

    public void checkValidWorlds() {
        for(Fort fort : new ArrayList<>(getForts())) {
            if(fort.getPosition().getWorld() == null){
                delete(fort);
                TownsAndNations.getPlugin().getLogger().warning("Deleted fort " + fort.getID() + " due to invalid world.");
            }
        }
    }
}
