package org.leralix.tan.utils.gameplay;

import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;

public class TerritoryUtil {

    private TerritoryUtil() {
        throw new IllegalStateException("Utility class");
    }
    public static TerritoryData getTerritory(String id){


        if(id.startsWith("T")) {
            return TownDataStorage.getInstance().getSync(id);
        }
        if(id.startsWith("R")) {
            return RegionDataStorage.getInstance().getSync(id);
        }
        return null;
    }

}
