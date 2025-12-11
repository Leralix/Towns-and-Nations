package org.leralix.tan.redis;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.logging.Logger;
import org.leralix.tan.dataclass.territory.TerritoryData;

public class QueryCacheManager {

  private static final Logger logger = Logger.getLogger(QueryCacheManager.class.getName());
  private static final Gson gson = new Gson();

  private static Cache<String, Object> localCache;

  private static JedisManager redisClient;

  public static void initialize(JedisManager jedisClient) {
    redisClient = jedisClient;

    localCache =
        CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(3, TimeUnit.MINUTES)
            .recordStats()
            .build();

    // Initialize circuit breaker for Redis fault tolerance
    RedisCircuitBreaker.initialize();
    logger.info("[TaN-QueryCache] Initialized with circuit breaker protection");
  }

  public static CompletableFuture<List<Object>> getTransactionHistoryCached(
      TerritoryData territory,
      String transactionType,
      Function<TerritoryData, List<Object>> fetchFunction) {

    String cacheKey = "tan:cache:trans_history:" + territory.getID() + ":" + transactionType;

    return CompletableFuture.supplyAsync(
        () -> {
          @SuppressWarnings("unchecked")
          List<Object> cached = (List<Object>) localCache.getIfPresent(cacheKey);
          if (cached != null) {
            return cached;
          }

          if (redisClient != null) {
            // Circuit breaker protects against Redis failures
            String jsonValue = RedisCircuitBreaker.execute(
                () -> redisClient.hashGet("tan:query_cache", cacheKey),
                () -> {
                  logger.fine("[TaN-QueryCache] Redis circuit OPEN - skipping L2 cache read");
                  return null; // Fallback: skip Redis, fetch from MySQL
                });

            if (jsonValue != null) {
              @SuppressWarnings("unchecked")
              List<Object> redisCached = (List<Object>) gson.fromJson(jsonValue, List.class);
              cached = redisCached;
              localCache.put(cacheKey, cached);
              return cached;
            }
          }

          List<Object> result = fetchFunction.apply(territory);

          localCache.put(cacheKey, result);

          if (redisClient != null) {
            int ttlMinutes = transactionType.equals("TAXATION") ? 30 : 5;
            String jsonValue = gson.toJson(result);

            // Circuit breaker protects against Redis failures
            RedisCircuitBreaker.executeVoid(
                () -> {
                  redisClient.hashSet("tan:query_cache", cacheKey, jsonValue);
                  redisClient.expire("tan:query_cache", ttlMinutes * 60);
                },
                () -> logger.fine("[TaN-QueryCache] Redis circuit OPEN - skipping L2 cache write")
            );
          }

          return result;
        });
  }

  public static int getPlayerBalance(UUID playerUUID, Function<UUID, Integer> fetchFunction) {
    String cacheKey = "tan:cache:balance:" + playerUUID;

    Integer cached = (Integer) localCache.getIfPresent(cacheKey);
    if (cached != null) {
      logger.fine("[TaN-QueryCache] L1 HIT: " + cacheKey);
      return cached;
    }

    if (redisClient != null) {
      String jsonValue = RedisCircuitBreaker.execute(
          () -> redisClient.hashGet("tan:query_cache", cacheKey),
          () -> {
            logger.fine("[TaN-QueryCache] Redis circuit OPEN - skipping L2 read for balance");
            return null;
          });

      if (jsonValue != null) {
        cached = gson.fromJson(jsonValue, Integer.class);
        logger.fine("[TaN-QueryCache] L2 HIT: " + cacheKey);
        localCache.put(cacheKey, cached);
        return cached;
      }
    }

    logger.fine("[TaN-QueryCache] MISS: " + cacheKey);
    int balance = fetchFunction.apply(playerUUID);

    localCache.put(cacheKey, balance);

    if (redisClient != null) {
      String jsonValue = gson.toJson(balance);
      RedisCircuitBreaker.executeVoid(
          () -> {
            redisClient.hashSet("tan:query_cache", cacheKey, jsonValue);
            redisClient.expire("tan:query_cache", 60);
          },
          () -> logger.fine("[TaN-QueryCache] Redis circuit OPEN - skipping L2 write for balance")
      );
    }

    return balance;
  }

  public static TerritoryData getTerritoryData(
      String territoryId, Function<String, TerritoryData> fetchFunction) {

    String cacheKey = "tan:cache:territory:" + territoryId;

    TerritoryData cached = (TerritoryData) localCache.getIfPresent(cacheKey);
    if (cached != null) {
      logger.fine("[TaN-QueryCache] L1 HIT: " + cacheKey);
      return cached;
    }

    if (redisClient != null) {
      String jsonValue = RedisCircuitBreaker.execute(
          () -> redisClient.hashGet("tan:query_cache", cacheKey),
          () -> {
            logger.fine("[TaN-QueryCache] Redis circuit OPEN - skipping L2 read for territory");
            return null;
          });

      if (jsonValue != null) {
        cached = gson.fromJson(jsonValue, TerritoryData.class);
        logger.fine("[TaN-QueryCache] L2 HIT: " + cacheKey);
        localCache.put(cacheKey, cached);
        return cached;
      }
    }

    logger.fine("[TaN-QueryCache] MISS: " + cacheKey);
    TerritoryData territory = fetchFunction.apply(territoryId);

    localCache.put(cacheKey, territory);

    if (redisClient != null) {
      String jsonValue = gson.toJson(territory);
      RedisCircuitBreaker.executeVoid(
          () -> {
            redisClient.hashSet("tan:query_cache", cacheKey, jsonValue);
            redisClient.expire("tan:query_cache", 600);
          },
          () -> logger.fine("[TaN-QueryCache] Redis circuit OPEN - skipping L2 write for territory")
      );
    }

    return territory;
  }

  public static void invalidateTransactionHistory(String territoryId) {
    if (localCache != null) {
      localCache
          .asMap()
          .keySet()
          .removeIf(key -> key.startsWith("tan:cache:trans_history:" + territoryId));
    }

    if (redisClient != null) {
      RedisCircuitBreaker.executeVoid(
          () -> {
            var allFields = redisClient.hashGetAll("tan:query_cache");
            var keysToDelete =
                allFields.keySet().stream()
                    .filter(key -> key.startsWith("tan:cache:trans_history:" + territoryId))
                    .toArray(String[]::new);
            if (keysToDelete.length > 0) {
              redisClient.hashDelete("tan:query_cache", keysToDelete);
            }
          },
          () -> logger.fine("[TaN-QueryCache] Redis circuit OPEN - skipping L2 invalidation for transaction history")
      );
    }
  }

  public static void invalidateTerritories(java.util.List<String> territoryIds) {
    if (territoryIds == null || territoryIds.isEmpty()) {
      return;
    }

    if (localCache != null) {
      for (String territoryId : territoryIds) {
        localCache.invalidate("tan:cache:territory:" + territoryId);
      }
    }

    if (redisClient != null) {
      String[] keysToDelete =
          territoryIds.stream().map(id -> "tan:cache:territory:" + id).toArray(String[]::new);
      if (keysToDelete.length > 0) {
        RedisCircuitBreaker.executeVoid(
            () -> redisClient.hashDelete("tan:query_cache", keysToDelete),
            () -> logger.fine("[TaN-QueryCache] Redis circuit OPEN - skipping L2 batch invalidation for territories")
        );
      }
    }
  }

  public static void invalidatePlayerBalance(UUID playerUUID) {
    String cacheKey = "tan:cache:balance:" + playerUUID;

    if (localCache != null) {
      localCache.invalidate(cacheKey);
    }

    if (redisClient != null) {
      RedisCircuitBreaker.executeVoid(
          () -> redisClient.hashDelete("tan:query_cache", cacheKey),
          () -> logger.fine("[TaN-QueryCache] Redis circuit OPEN - skipping L2 invalidation for balance")
      );
    }
  }

  public static void invalidateTerritory(String territoryId) {
    String cacheKey = "tan:cache:territory:" + territoryId;

    if (localCache != null) {
      localCache.invalidate(cacheKey);
    }

    if (redisClient != null) {
      RedisCircuitBreaker.executeVoid(
          () -> redisClient.hashDelete("tan:query_cache", cacheKey),
          () -> logger.fine("[TaN-QueryCache] Redis circuit OPEN - skipping L2 invalidation for territory")
      );
    }
  }

  public static void clearAllCaches() {
    if (localCache != null) {
      localCache.invalidateAll();
    }

    if (redisClient != null) {
      try {
        redisClient.delete("tan:query_cache");
      } catch (Exception e) {
      }
    }
  }

  public static String getCacheStats() {
    if (localCache == null) {
      return "Cache not initialized";
    }

    var stats = localCache.stats();
    double hitRate = stats.hitRate() * 100;

    return String.format(
        "L1 Cache - Hits: %d | Misses: %d | Hit Rate: %.1f%% | Size: %d",
        stats.hitCount(), stats.missCount(), hitRate, localCache.size());
  }
}
