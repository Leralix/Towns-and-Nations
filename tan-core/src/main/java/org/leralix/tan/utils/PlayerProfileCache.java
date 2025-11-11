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

/**
 * Cache for PlayerProfile to prevent Mojang API rate limiting (429 errors).
 *
 * <p>This cache stores PlayerProfile objects with a TTL and implements rate limiting to avoid
 * making too many requests to Mojang's API. When multiple GUIs open simultaneously with player
 * heads, this prevents the "Status: 429 Too Many Requests" error.
 *
 * <p>Features: - LRU cache with configurable size (default: 500 profiles) - TTL of 1 hour for
 * cached profiles - Rate limiting: max 20 requests per minute - Async profile loading to prevent
 * thread blocking
 */
public class PlayerProfileCache {

  private static final Logger LOGGER = LoggerFactory.getLogger(PlayerProfileCache.class);
  private static PlayerProfileCache instance;

  // Cache configuration
  private static final int CACHE_SIZE = 500;
  private static final long CACHE_TTL_MS = TimeUnit.HOURS.toMillis(1); // 1 hour

  // Rate limiting configuration
  private static final int MAX_REQUESTS_PER_MINUTE = 20;
  private static final long RATE_LIMIT_WINDOW_MS = TimeUnit.MINUTES.toMillis(1);

  // LRU cache for profiles
  private final Map<UUID, CachedProfile> profileCache =
      new LinkedHashMap<UUID, CachedProfile>(CACHE_SIZE + 1, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<UUID, CachedProfile> eldest) {
          return size() > CACHE_SIZE;
        }
      };

  // Rate limiting state
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

  /**
   * Get a PlayerProfile from cache or fetch it asynchronously if not cached.
   *
   * @param player The offline player
   * @return CompletableFuture with the PlayerProfile, or null if rate limited
   */
  public CompletableFuture<PlayerProfile> getProfileAsync(OfflinePlayer player) {
    UUID uuid = player.getUniqueId();

    // Check cache first
    synchronized (profileCache) {
      CachedProfile cached = profileCache.get(uuid);
      if (cached != null && !cached.isExpired()) {
        LOGGER.debug("Profile cache HIT for {}", player.getName());
        return CompletableFuture.completedFuture(cached.profile);
      }
    }

    // Check rate limiting
    if (!canMakeRequest(uuid)) {
      LOGGER.warn("Rate limit exceeded for profile fetch: {} - Using fallback", player.getName());
      return CompletableFuture.completedFuture(createFallbackProfile(player));
    }

    // Fetch async
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

  /**
   * Get a PlayerProfile synchronously (uses cache or creates fallback).
   *
   * <p>WARNING: This method will NOT make network requests to avoid blocking. If the profile is not
   * cached, it returns a basic profile without textures.
   *
   * @param player The offline player
   * @return PlayerProfile from cache or basic fallback profile
   */
  public PlayerProfile getProfileSync(OfflinePlayer player) {
    UUID uuid = player.getUniqueId();

    // Check cache
    synchronized (profileCache) {
      CachedProfile cached = profileCache.get(uuid);
      if (cached != null && !cached.isExpired()) {
        return cached.profile;
      }
    }

    // Return fallback without making network request
    LOGGER.debug(
        "Profile not in cache for {} - Using fallback (no network request)", player.getName());
    return createFallbackProfile(player);
  }

  /**
   * Cache a PlayerProfile.
   *
   * @param uuid Player UUID
   * @param profile PlayerProfile to cache
   */
  private void cacheProfile(UUID uuid, PlayerProfile profile) {
    synchronized (profileCache) {
      profileCache.put(uuid, new CachedProfile(profile));
      LOGGER.debug("Cached profile for UUID: {}", uuid);
    }
  }

  /**
   * Check if a request can be made without exceeding rate limits.
   *
   * @param uuid Player UUID
   * @return true if request is allowed
   */
  private boolean canMakeRequest(UUID uuid) {
    long now = System.currentTimeMillis();

    synchronized (this) {
      // Reset window if needed
      if (now - windowStartTime > RATE_LIMIT_WINDOW_MS) {
        requestsInCurrentWindow = 0;
        windowStartTime = now;
        requestTimestamps.clear();
      }

      // Check global limit
      if (requestsInCurrentWindow >= MAX_REQUESTS_PER_MINUTE) {
        LOGGER.warn(
            "Global rate limit reached: {}/{} requests in current window",
            requestsInCurrentWindow,
            MAX_REQUESTS_PER_MINUTE);
        return false;
      }

      // Check per-player limit (no more than 1 request per 3 seconds per player)
      Long lastRequest = requestTimestamps.get(uuid);
      if (lastRequest != null && (now - lastRequest) < 3000) {
        return false;
      }

      // Allow request
      requestsInCurrentWindow++;
      requestTimestamps.put(uuid, now);
      return true;
    }
  }

  /**
   * Create a fallback profile without textures (doesn't make network requests).
   *
   * @param player The offline player
   * @return Basic PlayerProfile
   */
  private PlayerProfile createFallbackProfile(OfflinePlayer player) {
    return Bukkit.createPlayerProfile(player.getUniqueId(), player.getName());
  }

  /** Clear the entire cache. */
  public void clearCache() {
    synchronized (profileCache) {
      profileCache.clear();
      LOGGER.info("PlayerProfile cache cleared");
    }
  }

  /**
   * Get cache statistics.
   *
   * @return Cache stats string
   */
  public String getCacheStats() {
    synchronized (profileCache) {
      return String.format(
          "Cache size: %d/%d, Requests in window: %d/%d",
          profileCache.size(), CACHE_SIZE, requestsInCurrentWindow, MAX_REQUESTS_PER_MINUTE);
    }
  }

  /** Cached profile with expiration time. */
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
