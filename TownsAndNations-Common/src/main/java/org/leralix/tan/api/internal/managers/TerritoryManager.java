package org.leralix.tan.api.internal.managers;

import org.leralix.tan.storage.stored.NationDataStorage;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.tan.api.getters.TanTerritoryManager;
import org.tan.api.interfaces.territory.TanNation;
import org.tan.api.interfaces.territory.TanRegion;
import org.tan.api.interfaces.territory.TanTown;

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
    private final NationDataStorage nationDataStorageInstance;

    private static TerritoryManager instance;

    private TerritoryManager() {
        townDataStorageInstance = TownDataStorage.getInstance();
        regionDataStorageInstance = RegionDataStorage.getInstance();
        nationDataStorageInstance = NationDataStorage.getInstance();
    }

    public static TerritoryManager getInstance() {
        if (instance == null) {
            instance = new TerritoryManager();
        }
        return instance;
    }


    @Override
    public Optional<TanTown> getTown(String uuid) {
        return Optional.ofNullable(townDataStorageInstance.get(uuid));
    }

    @Override
    public Optional<TanTown> getTownByName(String s) {
        return Optional.empty();
    }

    @Override
    public Collection<TanTown> getTowns() {
        return townDataStorageInstance.getAll().values().stream()
                .map(t -> (TanTown) t)
                .toList();
    }

    @Override
    public Optional<TanRegion> getRegion(String uuid) {
        return Optional.ofNullable(regionDataStorageInstance.get(uuid));
    }

    @Override
    public Optional<TanRegion> getRegionByName(String s) {
        return Optional.empty();
    }

    @Override
    public Collection<TanRegion> getRegions() {
        return regionDataStorageInstance.getAll().values().stream()
                .map(TanRegion.class::cast)
                .toList();
    }

    @Override
    public Optional<TanNation> getNation(String nationID) {
        return Optional.ofNullable(nationDataStorageInstance.get(nationID));
    }

    @Override
    public Optional<TanNation> getNationByName(String nationName) {
        return Optional.empty();
    }

    @Override
    public Collection<TanNation> getNations() {
        return nationDataStorageInstance.getAll().values().stream()
                .map(TanNation.class::cast)
                .toList();
    }
}
