package org.leralix.tan.utils.gameplay;

import org.jetbrains.annotations.NotNull;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.gui.scope.BrowseScope;
import org.leralix.tan.storage.stored.NationDataStorage;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
        if (id.startsWith("N")) {
            return NationDataStorage.getInstance().get(id);
        }
        return null;
    }

    public static boolean isNameUsed(String name, Collection<? extends TerritoryData> territories){
        String territoryName = name.replaceAll(" ", "-");
        for(TerritoryData territory : territories){
            if(territoryName.equals(territory.getName().replaceAll(" ", "-"))){
                return true;
            }
        }
        return false;
    }

    public static @NotNull List<TerritoryData> getTerritories(BrowseScope scope) {
        List<TerritoryData> territoryList = new ArrayList<>();

        if(scope == BrowseScope.ALL || scope == BrowseScope.TOWNS)
            territoryList.addAll(TownDataStorage.getInstance().getAll().values());
        if(scope == BrowseScope.ALL || scope == BrowseScope.REGIONS)
            territoryList.addAll(RegionDataStorage.getInstance().getAll().values());
        if(scope == BrowseScope.ALL || scope == BrowseScope.NATIONS && org.leralix.tan.utils.constants.Constants.enableNation())
            territoryList.addAll(org.leralix.tan.storage.stored.NationDataStorage.getInstance().getAll().values());
        return territoryList;
    }

}
