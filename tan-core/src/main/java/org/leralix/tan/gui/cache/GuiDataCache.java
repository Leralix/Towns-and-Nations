package org.leralix.tan.gui.cache;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.leralix.tan.gui.monitoring.GuiPerformanceMonitor;

public class GuiDataCache {

  private static final GuiDataCache INSTANCE = new GuiDataCache();

  private static final long DEFAULT_TTL_MS = TimeUnit.MINUTES.toMillis(5);

  private final Map<String, CacheEntry<?>> cache = new ConcurrentHashMap<>();

  private int maxCacheSize = 1000;

  private boolean enabled = true;

  private GuiDataCache() {
    startCleanupTask();
  }

  public static GuiDataCache getInstance() {
    return INSTANCE;
  }

  public <T> CompletableFuture<T> getOrCompute(
      String key, Supplier<CompletableFuture<T>> supplier, String guiName) {
    return getOrCompute(key, supplier, guiName, DEFAULT_TTL_MS);
  }

  @SuppressWarnings("unchecked")
  public <T> CompletableFuture<T> getOrCompute(
      String key, Supplier<CompletableFuture<T>> supplier, String guiName, long ttlMs) {

    if (!enabled) {
      return supplier.get();
    }

    CacheEntry<?> entry = cache.get(key);

    if (entry != null && !entry.isExpired()) {
      GuiPerformanceMonitor.getInstance().recordCacheHit(guiName);
      return CompletableFuture.completedFuture((T) entry.value);
    }

    GuiPerformanceMonitor.getInstance().recordCacheMiss(guiName);

    return supplier
        .get()
        .thenApply(
            value -> {
              put(key, value, ttlMs);
              return value;
            });
  }

  public <T> void put(String key, T value, long ttlMs) {
    if (!enabled) return;

    if (cache.size() >= maxCacheSize) {
      evictOldestEntries();
    }

    cache.put(key, new CacheEntry<>(value, System.currentTimeMillis() + ttlMs));
  }

  public <T> void put(String key, T value) {
    put(key, value, DEFAULT_TTL_MS);
  }

  public void invalidate(String key) {
    cache.remove(key);
  }

  public void invalidatePlayer(UUID playerId) {
    String prefix = "player:" + playerId;
    cache.keySet().removeIf(key -> key.startsWith(prefix));
  }

  public void invalidateTown(String townId) {
    String prefix = "town:" + townId;
    cache.keySet().removeIf(key -> key.startsWith(prefix));
  }

  public void invalidateRegion(String regionId) {
    String prefix = "region:" + regionId;
    cache.keySet().removeIf(key -> key.startsWith(prefix));
  }

  public void clear() {
    cache.clear();
  }

  public int size() {
    return cache.size();
  }

  public void setMaxCacheSize(int maxCacheSize) {
    this.maxCacheSize = maxCacheSize;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
    if (!enabled) {
      clear();
    }
  }

  public boolean isEnabled() {
    return enabled;
  }

  private void evictOldestEntries() {
    int toRemove = maxCacheSize / 10;
    cache.entrySet().stream()
        .sorted((a, b) -> Long.compare(a.getValue().expiresAt, b.getValue().expiresAt))
        .limit(toRemove)
        .forEach(entry -> cache.remove(entry.getKey()));
  }

  private void startCleanupTask() {
    Thread cleanupThread =
        new Thread(
            () -> {
              while (true) {
                try {
                  Thread.sleep(TimeUnit.MINUTES.toMillis(1));
                  cleanupExpiredEntries();
                } catch (InterruptedException e) {
                  Thread.currentThread().interrupt();
                  break;
                }
              }
            },
            "GuiDataCache-Cleanup");
    cleanupThread.setDaemon(true);
    cleanupThread.start();
  }

  private void cleanupExpiredEntries() {
    cache.entrySet().removeIf(entry -> entry.getValue().isExpired());
  }

  public String getStats() {
    long expired = cache.values().stream().filter(CacheEntry::isExpired).count();
    return String.format(
        "Cache Stats: %,d entries (%,d expired) | Max: %,d | Enabled: %s",
        cache.size(), expired, maxCacheSize, enabled);
  }

  private static class CacheEntry<T> {
    final T value;
    final long expiresAt;

    CacheEntry(T value, long expiresAt) {
      this.value = value;
      this.expiresAt = expiresAt;
    }

    boolean isExpired() {
      return System.currentTimeMillis() > expiresAt;
    }
  }

  public static class Keys {
    public static String playerData(UUID playerId) {
      return "player:" + playerId;
    }

    public static String townData(String townId) {
      return "town:" + townId;
    }

    public static String territoryData(String territoryId) {
      return "territory:" + territoryId;
    }

    public static String regionData(String regionId) {
      return "region:" + regionId;
    }

    public static String playerTowns(UUID playerId) {
      return "player:" + playerId + ":towns";
    }

    public static String townMembers(String townId) {
      return "town:" + townId + ":members";
    }

    public static String townTerritories(String townId) {
      return "town:" + townId + ":territories";
    }

    public static String regionTerritories(String regionId) {
      return "region:" + regionId + ":territories";
    }
  }
}
