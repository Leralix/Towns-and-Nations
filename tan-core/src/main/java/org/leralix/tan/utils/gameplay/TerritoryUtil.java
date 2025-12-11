package org.leralix.tan.utils.gameplay;

import java.util.concurrent.CompletableFuture;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;

public class TerritoryUtil {

  private TerritoryUtil() {
    throw new IllegalStateException("Utility class");
  }

  @Deprecated
  public static TerritoryData getTerritory(String id) {

    if (id.startsWith("T")) {
      return TownDataStorage.getInstance().get(id).join();
    }
    if (id.startsWith("R")) {
      return RegionDataStorage.getInstance().get(id).join();
    }
    return null;
  }

  public static CompletableFuture<TerritoryData> getTerritoryAsync(String id) {
    if (id.startsWith("T")) {
      return TownDataStorage.getInstance().get(id).thenApply(town -> (TerritoryData) town);
    }
    if (id.startsWith("R")) {
      return RegionDataStorage.getInstance().get(id).thenApply(region -> (TerritoryData) region);
    }
    return CompletableFuture.completedFuture(null);
  }
}
