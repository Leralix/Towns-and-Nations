package org.tan.api.getters;

import java.util.Collection;
import java.util.Optional;
import org.tan.api.interfaces.TanRegion;
import org.tan.api.interfaces.TanTown;

public interface TanTerritoryManager {
  Optional<TanTown> getTown(String id);

  Optional<TanTown> getTownByName(String name);

  Collection<TanTown> getTowns();

  Optional<TanRegion> getRegion(String id);

  Optional<TanRegion> getRegionByName(String name);

  Collection<TanRegion> getRegions();
}
