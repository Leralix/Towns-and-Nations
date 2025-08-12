package org.leralix.tan.api.managers;

import org.leralix.tan.api.wrappers.RegionDataWrapper;
import org.leralix.tan.api.wrappers.TownDataWrapper;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.tan.api.getters.TanTerritoryManager;
import org.tan.api.interfaces.TanRegion;
import org.tan.api.interfaces.TanTown;

import java.util.Collection;
import java.util.Optional;

/**
 * Placeholder for TanTerritoryManager <br>
 * This allows a single entry point for all territory related operations, It
 * stores the instance of both the {@link TownDataStorage}
 * and {@link RegionDataStorage}
 */
public class TerritoryManager implements TanTerritoryManager {
    private final TownDataStorage townDataStorageInstance;
    private final RegionDataStorage regionDataStorageInstance;

    private static TerritoryManager instance;

    private TerritoryManager() {
        townDataStorageInstance = TownDataStorage.getInstance();
        regionDataStorageInstance = RegionDataStorage.getInstance();
    }

    public static TerritoryManager getInstance() {
        if (instance == null) {
            instance = new TerritoryManager();
        }
        return instance;
    }


    @Override
    public Optional<TanTown> getTown(String uuid) {
        TownDataWrapper townDataWrapper = TownDataWrapper.of(townDataStorageInstance.get(uuid));
        return Optional.ofNullable(townDataWrapper);
    }

    @Override
    public Optional<TanTown> getTownByName(String s) {
        return Optional.empty();
    }

    @Override
    public Collection<TanTown> getTowns() {
        return townDataStorageInstance.getAll().values().stream()
                .map(TownDataWrapper::of)
                .map(t -> (TanTown) t)
                .toList();
    }

    @Override
    public Optional<TanRegion> getRegion(String uuid) {
        RegionDataWrapper regionDataWrapper = RegionDataWrapper.of(regionDataStorageInstance.get(uuid));
        return Optional.ofNullable(regionDataWrapper);
    }

    @Override
    public Optional<TanRegion> getRegionByName(String s) {
        return Optional.empty();
    }

    @Override
    public Collection<TanRegion> getRegions() {
        return regionDataStorageInstance.getAll().values().stream()
                .map(RegionDataWrapper::of)
                .map(TanRegion.class::cast)
                .toList();
    }
}
