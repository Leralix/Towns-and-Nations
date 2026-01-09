package org.leralix.tan.api.internal.wrappers;

import org.leralix.lib.position.Vector2D;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.storage.stored.LandmarkStorage;
import org.tan.api.interfaces.TanLandmark;
import org.tan.api.interfaces.TanProperty;
import org.tan.api.interfaces.TanTown;

import java.util.Collection;
import java.util.Optional;

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
    public int getLevel() {
        return townData.getNewLevel().getMainLevel();
    }

    @Override
    public Collection<TanProperty> getProperties() {
        return townData.getPropertyDataMap().values().stream()
                .map(PropertyDataWrapper::of)
                .toList();
    }

    @Override
    public Collection<TanLandmark> getLandmarksOwned() {
        return LandmarkStorage.getInstance().getLandmarkOf(townData).stream()
                .map(LandmarkDataWrapper::of)
                .toList();
    }

    @Override
    public Optional<Vector2D> getCapitalLocation() {
        return townData.getCapitalLocation();
    }
}
