package org.leralix.tan.utils.gameplay;

import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.storage.stored.KingdomDataStorage;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.constants.Constants;

public class TerritoryUtil {

    private TerritoryUtil() {
        throw new IllegalStateException("Utility class");
    }
    public static TerritoryData getTerritory(String id){
        if(id.startsWith("T")) {
            return TownDataStorage.getInstance().get(id);
        }
        if(id.startsWith("R")) {
            return RegionDataStorage.getInstance().get(id);
        }
        if (Constants.enableNation() && id.startsWith("N")) {
            return org.leralix.tan.storage.stored.NationDataStorage.getInstance().get(id);
        }
        return null;
    }

}
