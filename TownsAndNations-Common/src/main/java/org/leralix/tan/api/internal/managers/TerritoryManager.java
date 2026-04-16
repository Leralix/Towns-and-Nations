package org.leralix.tan.api.internal.managers;

import org.leralix.tan.storage.stored.NationStorage;
import org.leralix.tan.storage.stored.RegionStorage;
import org.leralix.tan.storage.stored.TownStorage;
import org.leralix.tan.storage.stored.json.RegionDataStorage;
import org.tan.api.getters.TanTerritoryManager;
import org.tan.api.interfaces.territory.TanNation;
import org.tan.api.interfaces.territory.TanRegion;
import org.tan.api.interfaces.territory.TanTown;

import java.util.Collection;
import java.util.Optional;

/**
 * Placeholder for TanTerritoryManager <br>
 * This allows a single entry point for all territory related operations, It
 * stores the instance of both the {@link TownStorage}
 * and {@link RegionDataStorage}
 */
public class TerritoryManager implements TanTerritoryManager {
    private final TownStorage townStorageInstance;
    private final RegionStorage regionStorageInstance;
    private final NationStorage nationStorageInstance;

    public TerritoryManager(TownStorage townStorage, RegionStorage regionStorage, NationStorage nationStorage) {
        townStorageInstance = townStorage;
        regionStorageInstance = regionStorage;
        nationStorageInstance = nationStorage;
    }


    @Override
    public Optional<TanTown> getTown(String uuid) {
        return Optional.ofNullable(townStorageInstance.get(uuid));
    }

    @Override
    public Optional<TanTown> getTownByName(String s) {
        return Optional.empty();
    }

    @Override
    public Collection<TanTown> getTowns() {
        return townStorageInstance.getAll().values().stream()
                .map(t -> (TanTown) t)
                .toList();
    }

    @Override
    public Optional<TanRegion> getRegion(String uuid) {
        return Optional.ofNullable(regionStorageInstance.get(uuid));
    }

    @Override
    public Optional<TanRegion> getRegionByName(String s) {
        return Optional.empty();
    }

    @Override
    public Collection<TanRegion> getRegions() {
        return regionStorageInstance.getAll().values().stream()
                .map(TanRegion.class::cast)
                .toList();
    }

    @Override
    public Optional<TanNation> getNation(String nationID) {
        return Optional.ofNullable(nationStorageInstance.get(nationID));
    }

    @Override
    public Optional<TanNation> getNationByName(String nationName) {
        return Optional.empty();
    }

    @Override
    public Collection<TanNation> getNations() {
        return nationStorageInstance.getAll().values().stream()
                .map(TanNation.class::cast)
                .toList();
    }
}
