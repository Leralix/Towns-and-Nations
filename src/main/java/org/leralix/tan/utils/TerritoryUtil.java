package org.leralix.tan.utils;

import org.leralix.tan.dataclass.territory.ITerritoryData;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;

public class TerritoryUtil {

    private TerritoryUtil() {
        throw new IllegalStateException("Utility class");
    }
    public static ITerritoryData getTerritory(String id){


        if(id.startsWith("T")) {
            return TownDataStorage.get(id);
        }
        if(id.startsWith("R")) {
            return RegionDataStorage.get(id);
        }
        return null;
    }

}
