package org.leralix.tan.api.wrappers;

import org.leralix.tan.dataclass.territory.TownData;
import org.tan.api.interfaces.TanLandmark;
import org.tan.api.interfaces.TanProperty;
import org.tan.api.interfaces.TanTown;

import java.util.Collection;

public class TownDataWrapper extends TerritoryDataWrapper implements TanTown {

    private final TownData townData;

    private TownDataWrapper(TownData townData){
        super(townData);
        this.townData = townData;
    }

    public static TownDataWrapper of(TownData townData){
        if(townData == null)
            return null;
        return new TownDataWrapper(townData);
    }

    @Override
    public Collection<TanProperty> getProperties() {
        return townData.getPropertyDataMap().values().stream()
                .map(PropertyDataWrapper::of)
                .map(p -> (TanProperty) p)
                .toList();
    }

    @Override
    public Collection<TanLandmark> getLandmarksOwned() {
        return townData.getOwnedLandmarks().stream()
                .map(LandmarkDataWrapper::of)
                .map(l -> (TanLandmark) l)
                .toList();
    }
}
