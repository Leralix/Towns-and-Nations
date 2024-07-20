package org.tan.TownsAndNations.utils;

import org.tan.TownsAndNations.DataClass.territoryData.ITerritoryData;
import org.tan.TownsAndNations.storage.DataStorage.RegionDataStorage;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;

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
