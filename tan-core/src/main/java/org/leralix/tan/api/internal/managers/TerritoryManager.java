package org.leralix.tan.api.internal.managers;

import java.util.Collection;
import java.util.Optional;
import org.leralix.tan.api.internal.wrappers.RegionDataWrapper;
import org.leralix.tan.api.internal.wrappers.TownDataWrapper;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.tan.api.getters.TanTerritoryManager;
import org.tan.api.interfaces.TanRegion;
import org.tan.api.interfaces.TanTown;

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
    TownDataWrapper townDataWrapper = TownDataWrapper.of(townDataStorageInstance.getSync(uuid));
    return Optional.ofNullable(townDataWrapper);
  }

  @Override
  public Optional<TanTown> getTownByName(String s) {
    return Optional.empty();
  }

  @Override
  public Collection<TanTown> getTowns() {
    return townDataStorageInstance.getAllAsync().join().values().stream()
        .map(TownDataWrapper::of)
        .map(t -> (TanTown) t)
        .toList();
  }

  @Override
  public Optional<TanRegion> getRegion(String uuid) {
    RegionDataWrapper regionDataWrapper =
        RegionDataWrapper.of(regionDataStorageInstance.getSync(uuid));
    return Optional.ofNullable(regionDataWrapper);
  }

  @Override
  public Optional<TanRegion> getRegionByName(String s) {
    return Optional.empty();
  }

  @Override
  public Collection<TanRegion> getRegions() {
    return regionDataStorageInstance.getAllAsync().join().values().stream()
        .map(RegionDataWrapper::of)
        .map(TanRegion.class::cast)
        .toList();
  }
}
