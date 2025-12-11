package org.leralix.tan.utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.profile.PlayerProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerProfileCache {

  private static final Logger LOGGER = LoggerFactory.getLogger(PlayerProfileCache.class);
  private static PlayerProfileCache instance;

  private static final int CACHE_SIZE = 500;
  private static final long CACHE_TTL_MS = TimeUnit.HOURS.toMillis(1);

  private static final int MAX_REQUESTS_PER_MINUTE = 20;
  private static final long RATE_LIMIT_WINDOW_MS = TimeUnit.MINUTES.toMillis(1);

  private final Map<UUID, CachedProfile> profileCache =
      new LinkedHashMap<UUID, CachedProfile>(CACHE_SIZE + 1, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<UUID, CachedProfile> eldest) {
          return size() > CACHE_SIZE;
        }
      };

  private final Map<UUID, Long> requestTimestamps = new ConcurrentHashMap<>();
  private int requestsInCurrentWindow = 0;
  private long windowStartTime = System.currentTimeMillis();

  private PlayerProfileCache() {}

  public static PlayerProfileCache getInstance() {
    if (instance == null) {
      instance = new PlayerProfileCache();
    }
    return instance;
  }

  public CompletableFuture<PlayerProfile> getProfileAsync(OfflinePlayer player) {
    UUID uuid = player.getUniqueId();

    synchronized (profileCache) {
      CachedProfile cached = profileCache.get(uuid);
      if (cached != null && !cached.isExpired()) {
        LOGGER.debug("Profile cache HIT for {}", player.getName());
        return CompletableFuture.completedFuture(cached.profile);
      }
    }

    if (!canMakeRequest(uuid)) {
      LOGGER.warn("Rate limit exceeded for profile fetch: {} - Using fallback", player.getName());
      return CompletableFuture.completedFuture(createFallbackProfile(player));
    }

    LOGGER.debug("Profile cache MISS for {} - Fetching from Mojang API", player.getName());
    return CompletableFuture.supplyAsync(
        () -> {
          try {
            PlayerProfile profile = player.getPlayerProfile();
            if (profile != null) {
              cacheProfile(uuid, profile);
              return profile;
            }
          } catch (Exception e) {
            LOGGER.warn("Failed to fetch profile for {}: {}", player.getName(), e.getMessage());
          }
          return createFallbackProfile(player);
        });
  }

  public PlayerProfile getProfileSync(OfflinePlayer player) {
    UUID uuid = player.getUniqueId();

    synchronized (profileCache) {
      CachedProfile cached = profileCache.get(uuid);
      if (cached != null && !cached.isExpired()) {
        return cached.profile;
      }
    }

    LOGGER.debug(
        "Profile not in cache for {} - Using fallback (no network request)", player.getName());
    return createFallbackProfile(player);
  }

  private void cacheProfile(UUID uuid, PlayerProfile profile) {
    synchronized (profileCache) {
      profileCache.put(uuid, new CachedProfile(profile));
      LOGGER.debug("Cached profile for UUID: {}", uuid);
    }
  }

  private boolean canMakeRequest(UUID uuid) {
    long now = System.currentTimeMillis();

    synchronized (this) {
      if (now - windowStartTime > RATE_LIMIT_WINDOW_MS) {
        requestsInCurrentWindow = 0;
        windowStartTime = now;
        requestTimestamps.clear();
      }

      if (requestsInCurrentWindow >= MAX_REQUESTS_PER_MINUTE) {
        LOGGER.warn(
            "Global rate limit reached: {}/{} requests in current window",
            requestsInCurrentWindow,
            MAX_REQUESTS_PER_MINUTE);
        return false;
      }

      Long lastRequest = requestTimestamps.get(uuid);
      if (lastRequest != null && (now - lastRequest) < 3000) {
        return false;
      }

      requestsInCurrentWindow++;
      requestTimestamps.put(uuid, now);
      return true;
    }
  }

  private PlayerProfile createFallbackProfile(OfflinePlayer player) {
    return Bukkit.createPlayerProfile(player.getUniqueId(), player.getName());
  }

  public void clearCache() {
    synchronized (profileCache) {
      profileCache.clear();
      LOGGER.info("PlayerProfile cache cleared");
    }
  }

  public String getCacheStats() {
    synchronized (profileCache) {
      return String.format(
          "Cache size: %d/%d, Requests in window: %d/%d",
          profileCache.size(), CACHE_SIZE, requestsInCurrentWindow, MAX_REQUESTS_PER_MINUTE);
    }
  }

  private static class CachedProfile {
    final PlayerProfile profile;
    final long cacheTime;

    CachedProfile(PlayerProfile profile) {
      this.profile = profile;
      this.cacheTime = System.currentTimeMillis();
    }

    boolean isExpired() {
      return (System.currentTimeMillis() - cacheTime) > CACHE_TTL_MS;
    }
  }
}
