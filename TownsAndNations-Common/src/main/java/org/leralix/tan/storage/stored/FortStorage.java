package org.leralix.tan.storage.stored;

import org.leralix.lib.position.Vector3D;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.building.fort.Fort;
import org.leralix.tan.data.territory.Territory;

import java.util.ArrayList;
import java.util.List;

public interface FortStorage {

    /**
     * @return All foreign forts occupied by this territory. Not including forts owned by the territory
     */
    default List<Fort> getOccupiedFort(Territory territoryData) {
        List<Fort> res = new ArrayList<>();
        for(String fortID : territoryData.getOccupiedFortIds()) {
            Fort fort = getFort(fortID);
            if (fort == null) {
                continue;
            }
            res.add(fort);
        }
        return res;
    }

    /**
     * @return All forts owned by this territory, should they be occupied or not.
     */
    default List<Fort> getOwnedFort(Territory territoryData) {
        List<Fort> res = new ArrayList<>();
        for(String fortID : territoryData.getOwnedFortIDs()) {
            Fort fort = getFort(fortID);
            if (fort == null) {
                continue;
            }
            res.add(fort);
        }
        return res;
    }

    /**
     * @return All forts occupied by this territory, including forts owned by this territory
     * and excluding owned forts occupied by other territories
     */
    default List<Fort> getAllControlledFort(Territory territoryData) {
        List<Fort> allForts = new ArrayList<>(getOccupiedFort(territoryData));

        for(Fort fort : getOwnedFort(territoryData)) {
            if(!fort.isOccupied()){
                allForts.add(fort);
            }
        }
        return allForts;
    }

    List<Fort> getForts();

   Fort getFort(String fortID);

    Fort register(Vector3D position, Territory owningTerritory);

    void delete(String fortID);

    default void delete(Fort fort){
        fort.delete();
        delete(fort.getID());
    }

    void save();

    default void checkValidWorlds() {
        for(Fort fort : new ArrayList<>(getForts())) {
            if(fort.getPosition().getWorld() == null){
                delete(fort);
                TownsAndNations.getPlugin().getLogger().warning("Deleted fort " + fort.getID() + " due to invalid world.");
            }
        }
    }
}
