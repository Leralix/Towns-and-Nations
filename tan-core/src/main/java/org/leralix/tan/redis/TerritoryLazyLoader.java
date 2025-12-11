package org.leralix.tan.redis;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.logging.Logger;
import org.bukkit.plugin.Plugin;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.utils.FoliaScheduler;

public class TerritoryLazyLoader {

  private static final Logger logger = Logger.getLogger(TerritoryLazyLoader.class.getName());

  private static Cache<String, TerritoryData> territoryCache;

  private static final Set<String> loadingTerritories = new HashSet<>();

  private static int maxCachedTerritories = 5000;
  private static int unloadAfterMinutes = 10;

  public static void initialize(int maxTerritories, int evictionMinutes) {
    maxCachedTerritories = maxTerritories;
    unloadAfterMinutes = evictionMinutes;

    territoryCache =
        CacheBuilder.newBuilder()
            .maximumSize(maxCachedTerritories)
            .expireAfterAccess(unloadAfterMinutes, TimeUnit.MINUTES)
            .recordStats()
            .removalListener(
                (RemovalListener<String, TerritoryData>)
                    notification -> {
                      logger.fine(
                          "[TaN-LazyLoader] Evicted territory: "
                              + notification.getKey()
                              + " (Reason: "
                              + notification.getCause()
                              + ")");
                    })
            .build();
  }

  public static void initialize() {
    initialize(5000, 10);
  }

  public static TerritoryData getTerritory(
      String territoryId, Function<String, TerritoryData> loadFunction) {
    if (territoryCache == null) {
      return loadFunction.apply(territoryId);
    }

    TerritoryData cached = territoryCache.getIfPresent(territoryId);
    if (cached != null) {
      return cached;
    }

    synchronized (loadingTerritories) {
      if (loadingTerritories.contains(territoryId)) {
        while (loadingTerritories.contains(territoryId)) {
          try {
            loadingTerritories.wait(100);
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
          }
        }

        return territoryCache.getIfPresent(territoryId);
      }

      loadingTerritories.add(territoryId);
    }

    try {
      TerritoryData territory = loadFunction.apply(territoryId);

      if (territory != null) {
        territoryCache.put(territoryId, territory);
      }

      return territory;

    } finally {
      synchronized (loadingTerritories) {
        loadingTerritories.remove(territoryId);
        loadingTerritories.notifyAll();
      }
    }
  }

  public static CompletableFuture<Void> preloadTerritories(
      List<String> territoryIds, Function<String, TerritoryData> loadFunction) {

    if (territoryCache == null) {
      return CompletableFuture.completedFuture(null);
    }

    CompletableFuture<Void> result = new CompletableFuture<>();
    Plugin plugin = TownsAndNations.getPlugin();

    List<String> toLoad =
        territoryIds.stream().filter(id -> territoryCache.getIfPresent(id) == null).toList();

    if (toLoad.isEmpty()) {
      return CompletableFuture.completedFuture(null);
    }

    final int[] loadedCount = {0};
    final int total = toLoad.size();

    for (String id : toLoad) {
      FoliaScheduler.runTaskAsynchronously(
          plugin,
          () -> {
            try {
              getTerritory(id, loadFunction);

              synchronized (loadedCount) {
                loadedCount[0]++;

                if (loadedCount[0] >= total) {
                  FoliaScheduler.runTask(
                      plugin,
                      () -> {
                        logger.info("[TaN-LazyLoader] Pre-loaded " + total + " territories");
                        result.complete(null);
                      });
                }
              }
            } catch (Exception e) {
              logger.warning(
                  "[TaN-LazyLoader] Failed to preload territory " + id + ": " + e.getMessage());
              result.completeExceptionally(e);
            }
          });
    }

    return result;
  }

  public static void invalidateTerritory(String territoryId) {
    if (territoryCache != null) {
      territoryCache.invalidate(territoryId);
      logger.fine("[TaN-LazyLoader] Invalidated territory: " + territoryId);
    }
  }

  public static void invalidateTerritories(List<String> territoryIds) {
    if (territoryCache != null) {
      territoryIds.forEach(territoryCache::invalidate);
      logger.fine("[TaN-LazyLoader] Invalidated " + territoryIds.size() + " territories");
    }
  }

  public static void clearCache() {
    if (territoryCache != null) {
      territoryCache.invalidateAll();
      logger.info("[TaN-LazyLoader] Cleared all cached territories");
    }
  }

  public static String getStats() {
    if (territoryCache == null) {
      return "Lazy loader not initialized";
    }

    var stats = territoryCache.stats();
    double hitRate = stats.hitRate() * 100;
    double memoryMB = (territoryCache.size() * 2048) / (1024.0 * 1024.0);

    return String.format(
        "Cached: %d territories (~%.1f MB) | Hit Rate: %.1f%% | "
            + "Hits: %d | Misses: %d | Evictions: %d",
        territoryCache.size(),
        memoryMB,
        hitRate,
        stats.hitCount(),
        stats.missCount(),
        stats.evictionCount());
  }

  public static long getCachedCount() {
    return territoryCache != null ? territoryCache.size() : 0;
  }

  public static double getHitRate() {
    if (territoryCache == null) {
      return 0;
    }
    return territoryCache.stats().hitRate() * 100;
  }

  public static double getMemoryUsageMB() {
    if (territoryCache == null) {
      return 0;
    }
    return (territoryCache.size() * 2048) / (1024.0 * 1024.0);
  }

  public static boolean isCached(String territoryId) {
    return territoryCache != null && territoryCache.getIfPresent(territoryId) != null;
  }
}
