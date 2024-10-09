package org.leralix.tan.utils;

import org.leralix.tan.dataclass.territory.ITerritoryData;
import org.leralix.tan.storage.DataStorage.RegionDataStorage;
import org.leralix.tan.storage.DataStorage.TownDataStorage;

public class TerritoryUtil {

    public static ITerritoryData getTerritory(String ID){


        if(ID.startsWith("T")) {
            return TownDataStorage.get(ID);
        }
        if(ID.startsWith("R")) {
            return RegionDataStorage.get(ID);
        }
        return null;
    }

}
