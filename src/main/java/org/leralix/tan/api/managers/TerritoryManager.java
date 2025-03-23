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
import java.util.UUID;

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
        if(instance == null) {
            instance = new TerritoryManager();
        }
        return instance;
    }


    @Override
    public Optional<TanTown> getTown(UUID uuid) {
        TownDataWrapper townDataWrapper = TownDataWrapper.of(townDataStorageInstance.get(uuid.toString()));
        return Optional.ofNullable(townDataWrapper);
    }

    @Override
    public Optional<TanTown> getTown(String s) {
        return Optional.empty();
    }

    @Override
    public Collection<TanTown> getTowns() {
        return townDataStorageInstance.getAll().stream().map(TanTown.class::cast).toList();
    }

    @Override
    public Optional<TanRegion> getRegion(UUID uuid) {
        RegionDataWrapper regionDataWrapper = RegionDataWrapper.of(regionDataStorageInstance.get(uuid.toString()));
        return Optional.ofNullable(regionDataWrapper);
    }

    @Override
    public Optional<TanRegion> getRegion(String s) {
        return Optional.empty();
    }

    @Override
    public Collection<TanRegion> getRegions() {
        return regionDataStorageInstance.getAll().stream().map(TanRegion.class::cast).toList();
    }
}
