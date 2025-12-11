package org.tan_java.performance;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Player;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.PlayerDataStorage;

public final class PlayerLangCache {

  private static final PlayerLangCache INSTANCE = new PlayerLangCache();

  private static final long TTL_MS = 60_000;

  private final Map<UUID, CachedLang> cache = new ConcurrentHashMap<>();

  private long hits = 0;

  private long misses = 0;

  private PlayerLangCache() {}

  public static PlayerLangCache getInstance() {
    return INSTANCE;
  }

  public CompletableFuture<LangType> getLang(Player player) {
    if (player == null) {
      return CompletableFuture.completedFuture(LangType.ENGLISH);
    }

    return getLang(player.getUniqueId());
  }

  public CompletableFuture<LangType> getLang(UUID uuid) {
    if (uuid == null) {
      return CompletableFuture.completedFuture(LangType.ENGLISH);
    }

    CachedLang cached = cache.get(uuid);

    if (cached != null && !cached.isExpired()) {
      hits++;
      return CompletableFuture.completedFuture(cached.lang);
    }

    misses++;

    return PlayerDataStorage.getInstance()
        .get(uuid.toString())
        .thenApply(
            tanPlayer -> {
              if (tanPlayer == null) {
                return LangType.ENGLISH;
              }

              LangType lang = tanPlayer.getLang();

              cache.put(uuid, new CachedLang(lang, System.currentTimeMillis() + TTL_MS));

              return lang;
            })
        .exceptionally(
            ex -> {
              return LangType.ENGLISH;
            });
  }

  public void invalidate(UUID uuid) {
    if (uuid != null) {
      cache.remove(uuid);
    }
  }

  public void invalidate(Player player) {
    if (player != null) {
      invalidate(player.getUniqueId());
    }
  }

  public void invalidateAll() {
    cache.clear();
    resetStats();
  }

  public void cleanupExpired() {
    cache.entrySet().removeIf(entry -> entry.getValue().isExpired());
  }

  public double getHitRate() {
    long total = hits + misses;
    if (total == 0) {
      return 0.0;
    }
    return (double) hits / total * 100.0;
  }

  public long getHits() {
    return hits;
  }

  public long getMisses() {
    return misses;
  }

  public int getCacheSize() {
    return cache.size();
  }

  public void resetStats() {
    hits = 0;
    misses = 0;
  }

  private static final class CachedLang {
    final LangType lang;
    final long expiresAt;

    CachedLang(LangType lang, long expiresAt) {
      this.lang = lang;
      this.expiresAt = expiresAt;
    }

    boolean isExpired() {
      return System.currentTimeMillis() > expiresAt;
    }
  }
}
