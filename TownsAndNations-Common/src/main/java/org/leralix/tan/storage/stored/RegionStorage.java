package org.leralix.tan.storage.stored;

import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.Region;
import org.leralix.tan.data.territory.RegionData;
import org.leralix.tan.data.territory.Town;

import java.util.Map;

public interface RegionStorage extends IterritoryStorage{

    Region newRegion(String name, Town capital);

    default Region get(ITanPlayer tanPlayer){
        Town town = tanPlayer.getTown();
        if(town == null)
            return null;
        return town.getRegion().orElse(null);
    }

    default Region get(Town townData){
        if(townData == null){
            return null;
        }
        return get(townData.getRegionID());
    }

    void deleteRegion(RegionData region);

    Region get(String regionID);

    Map<String, Region> getAll();

    void save();
}
