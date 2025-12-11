package org.leralix.tan.redis;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.leralix.tan.utils.CocoLogger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

/**
 * Gestionnaire Redis utilisant Jedis. Fournit une API simplifiée pour remplacer Redisson.
 * Thread-safe et compatible Folia.
 */
public class JedisManager {

  private static final Logger logger = Logger.getLogger(JedisManager.class.getName());
  private static final Gson gson = new Gson();

  private final JedisPool jedisPool;
  private final ExecutorService pubSubExecutor;
  private final Map<String, JedisPubSub> activeSubscriptions;
  private final Map<String, String> luaScriptShas; // SHA-1 cache for EVALSHA optimization

  public JedisManager(
      String host, int port, String username, String password, int database, int poolSize) {
    JedisPoolConfig poolConfig = new JedisPoolConfig();
    poolConfig.setMaxTotal(poolSize);
    poolConfig.setMaxIdle(poolSize / 2);
    poolConfig.setMinIdle(10);
    poolConfig.setTestOnBorrow(true);
    poolConfig.setTestOnReturn(true);
    poolConfig.setTestWhileIdle(true);
    poolConfig.setMaxWaitMillis(3000);

    // Redis 6.0+ ACL support (username + password)
    if (username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
      this.jedisPool = new JedisPool(poolConfig, host, port, 3000, username, password, database);
      logger.info(CocoLogger.network("Jedis: Connexion avec ACL (username + password)"));
    } else if (password != null && !password.isEmpty()) {
      // Redis < 6.0 (password only)
      this.jedisPool = new JedisPool(poolConfig, host, port, 3000, password, database);
      logger.info(CocoLogger.network("Jedis: Connexion avec authentification (password)"));
    } else {
      this.jedisPool = new JedisPool(poolConfig, host, port, 3000, null, database);
      logger.info(CocoLogger.network("Jedis: Connexion sans authentification"));
    }

    this.pubSubExecutor =
        Executors.newCachedThreadPool(
            r -> {
              Thread t = new Thread(r, "TaN-Jedis-PubSub");
              t.setDaemon(true);
              return t;
            });

    this.activeSubscriptions = new ConcurrentHashMap<>();
    this.luaScriptShas = new ConcurrentHashMap<>();
    
    // Load and cache Lua scripts on startup
    loadLuaScripts();
    
    logger.info(CocoLogger.success("✓ JedisPool initialisé (pool: " + poolSize + ")"));
  }

  // ============ Opérations de base ============

  /** Test de connexion */
  public boolean testConnection() {
    try (Jedis jedis = jedisPool.getResource()) {
      String response = jedis.ping();
      logger.info(CocoLogger.success("✓ Redis PING réussi: " + response));
      return "PONG".equals(response);
    } catch (redis.clients.jedis.exceptions.JedisDataException e) {
      logger.severe(CocoLogger.error("Test connexion échoué (AUTH): " + e.getMessage()));
      logger.severe(
          CocoLogger.error(
              "Vérifiez le mot de passe/username dans config.yml - Redis rejette l'authentification"));
      return false;
    } catch (redis.clients.jedis.exceptions.JedisConnectionException e) {
      logger.severe(CocoLogger.error("Test connexion échoué (NETWORK): " + e.getMessage()));
      logger.severe(
          CocoLogger.error("Vérifiez que Redis est accessible sur le réseau (firewall, host, port)"));
      return false;
    } catch (Exception e) {
      logger.severe(CocoLogger.error("Test connexion échoué: " + e.getMessage()));
      e.printStackTrace();
      return false;
    }
  }

  /** SET avec TTL */
  public void set(String key, String value, long ttlSeconds) {
    try (Jedis jedis = jedisPool.getResource()) {
      if (ttlSeconds > 0) {
        jedis.setex(key, ttlSeconds, value);
      } else {
        jedis.set(key, value);
      }
    }
  }

  /** GET */
  public String get(String key) {
    try (Jedis jedis = jedisPool.getResource()) {
      return jedis.get(key);
    }
  }

  /** DELETE */
  public void delete(String... keys) {
    try (Jedis jedis = jedisPool.getResource()) {
      jedis.del(keys);
    }
  }

  /** EXISTS */
  public boolean exists(String key) {
    try (Jedis jedis = jedisPool.getResource()) {
      return jedis.exists(key);
    }
  }

  /** EXPIRE */
  public void expire(String key, long seconds) {
    try (Jedis jedis = jedisPool.getResource()) {
      jedis.expire(key, seconds);
    }
  }

  // ============ Sets (pour active servers) ============

  /** SADD */
  public void addToSet(String key, String... members) {
    try (Jedis jedis = jedisPool.getResource()) {
      jedis.sadd(key, members);
    }
  }

  /** SREM */
  public void removeFromSet(String key, String... members) {
    try (Jedis jedis = jedisPool.getResource()) {
      jedis.srem(key, members);
    }
  }

  /** SMEMBERS */
  public Set<String> getSetMembers(String key) {
    try (Jedis jedis = jedisPool.getResource()) {
      return jedis.smembers(key);
    }
  }

  /** SISMEMBER */
  public boolean isSetMember(String key, String member) {
    try (Jedis jedis = jedisPool.getResource()) {
      return jedis.sismember(key, member);
    }
  }

  // ============ Hashes (pour cache) ============

  /** HSET */
  public void hashSet(String key, String field, String value) {
    try (Jedis jedis = jedisPool.getResource()) {
      jedis.hset(key, field, value);
    }
  }

  /** HGET */
  public String hashGet(String key, String field) {
    try (Jedis jedis = jedisPool.getResource()) {
      return jedis.hget(key, field);
    }
  }

  /** HGETALL */
  public Map<String, String> hashGetAll(String key) {
    try (Jedis jedis = jedisPool.getResource()) {
      return jedis.hgetAll(key);
    }
  }

  /** HDEL */
  public void hashDelete(String key, String... fields) {
    try (Jedis jedis = jedisPool.getResource()) {
      jedis.hdel(key, fields);
    }
  }

  /** HEXISTS */
  public boolean hashExists(String key, String field) {
    try (Jedis jedis = jedisPool.getResource()) {
      return jedis.hexists(key, field);
    }
  }

  // ============ Pub/Sub ============

  /**
   * Subscribe à un channel avec un listener. Exécuté dans un thread séparé.
   *
   * @param channel Canal Redis
   * @param onMessage Callback appelé pour chaque message
   */
  public void subscribe(String channel, Consumer<String> onMessage) {
    pubSubExecutor.submit(
        () -> {
          JedisPubSub pubSub =
              new JedisPubSub() {
                @Override
                public void onMessage(String ch, String message) {
                  try {
                    onMessage.accept(message);
                  } catch (Exception e) {
                    logger.severe(
                        CocoLogger.error(
                            "Erreur traitement message [" + ch + "]: " + e.getMessage()));
                  }
                }

                @Override
                public void onSubscribe(String channel, int subscribedChannels) {
                  logger.info(
                      CocoLogger.network(
                          "⇄ Souscrit au canal: "
                              + channel
                              + " ("
                              + subscribedChannels
                              + " actifs)"));
                }

                @Override
                public void onUnsubscribe(String channel, int subscribedChannels) {
                  logger.info(CocoLogger.network("⊗ Désinscrit du canal: " + channel));
                }
              };

          activeSubscriptions.put(channel, pubSub);

          try (Jedis jedis = jedisPool.getResource()) {
            jedis.subscribe(pubSub, channel);
          } catch (Exception e) {
            logger.severe(
                CocoLogger.error("Erreur souscription [" + channel + "]: " + e.getMessage()));
          }
        });
  }

  /** Unsubscribe d'un channel */
  public void unsubscribe(String channel) {
    JedisPubSub pubSub = activeSubscriptions.remove(channel);
    if (pubSub != null && pubSub.isSubscribed()) {
      pubSub.unsubscribe(channel);
    }
  }

  /**
   * Publish un message sur un channel
   *
   * @return Nombre de subscribers qui ont reçu le message
   */
  public long publish(String channel, String message) {
    try (Jedis jedis = jedisPool.getResource()) {
      return jedis.publish(channel, message);
    }
  }

  /** Publish un objet JSON sur un channel */
  public long publishJson(String channel, Object object) {
    String json = gson.toJson(object);
    return publish(channel, json);
  }

  // ============ Opérations avancées ============

  /** Atomic increment */
  public long increment(String key) {
    try (Jedis jedis = jedisPool.getResource()) {
      return jedis.incr(key);
    }
  }

  /** Atomic decrement */
  public long decrement(String key) {
    try (Jedis jedis = jedisPool.getResource()) {
      return jedis.decr(key);
    }
  }

  /** KEYS pattern (utilisez avec précaution en production) */
  public Set<String> keys(String pattern) {
    try (Jedis jedis = jedisPool.getResource()) {
      return jedis.keys(pattern);
    }
  }

  /** Pipeline pour batch operations */
  public void executePipeline(Consumer<redis.clients.jedis.Pipeline> operations) {
    try (Jedis jedis = jedisPool.getResource()) {
      redis.clients.jedis.Pipeline pipeline = jedis.pipelined();
      operations.accept(pipeline);
      pipeline.sync();
    }
  }

  // ============ Shutdown ============

  /** Ferme toutes les connexions et arrête le pool */
  public void shutdown() {
    logger.info(CocoLogger.warning("Arrêt du gestionnaire Jedis..."));

    // Unsubscribe de tous les canaux
    activeSubscriptions.forEach(
        (channel, pubSub) -> {
          if (pubSub.isSubscribed()) {
            pubSub.unsubscribe();
          }
        });
    activeSubscriptions.clear();

    // Arrêter l'executor
    pubSubExecutor.shutdown();
    try {
      if (!pubSubExecutor.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS)) {
        pubSubExecutor.shutdownNow();
      }
    } catch (InterruptedException e) {
      pubSubExecutor.shutdownNow();
      Thread.currentThread().interrupt();
    }

    // Fermer le pool
    if (!jedisPool.isClosed()) {
      jedisPool.close();
    }

    logger.info(CocoLogger.success("✓ Jedis arrêté proprement"));
  }

  /** Vérifie si le manager est fermé */
  public boolean isClosed() {
    return jedisPool.isClosed();
  }

  /** Obtient le pool Jedis pour les opérations avancées */
  public JedisPool getPool() {
    return jedisPool;
  }

  // ============ Lua Script Support ============

  /**
   * Loads Lua scripts from resources and caches their SHA-1 hashes.
   * Scripts are loaded via SCRIPT LOAD for EVALSHA optimization.
   */
  private void loadLuaScripts() {
    String[] scripts = {
      "redis-lua/atomic_cache_publish.lua",
      "redis-lua/atomic_multi_invalidate.lua",
      "redis-lua/atomic_invalidate_publish.lua"
    };

    try (Jedis jedis = jedisPool.getResource()) {
      for (String scriptPath : scripts) {
        try {
          String scriptContent = loadLuaScriptFromResource(scriptPath);
          String sha = jedis.scriptLoad(scriptContent);
          luaScriptShas.put(scriptPath, sha);
          logger.info(
              CocoLogger.success("✓ Loaded Lua script: " + scriptPath + " (SHA: " + sha + ")"));
        } catch (Exception e) {
          logger.severe(
              CocoLogger.error("Failed to load Lua script: " + scriptPath + " - " + e.getMessage()));
        }
      }
    }
  }

  /**
   * Loads Lua script content from JAR resources.
   */
  private String loadLuaScriptFromResource(String resourcePath) throws Exception {
    try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
      if (is == null) {
        throw new IllegalArgumentException("Lua script not found: " + resourcePath);
      }
      try (BufferedReader reader =
          new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
        return reader.lines().collect(Collectors.joining("\n"));
      }
    }
  }

  /**
   * Executes Lua script atomically using EVALSHA (cached SHA-1).
   * Falls back to EVAL if script not found on Redis server.
   * 
   * @param scriptPath Path to script in resources (e.g., "redis-lua/atomic_cache_publish.lua")
   * @param keys Redis KEYS[] array
   * @param args Redis ARGV[] array
   * @return Script return value
   */
  public Object evalLua(String scriptPath, List<String> keys, List<String> args) {
    String sha = luaScriptShas.get(scriptPath);
    if (sha == null) {
      throw new IllegalArgumentException("Lua script not loaded: " + scriptPath);
    }

    try (Jedis jedis = jedisPool.getResource()) {
      try {
        // Try EVALSHA first (fastest - no script transfer)
        return jedis.evalsha(sha, keys, args);
      } catch (redis.clients.jedis.exceptions.JedisNoScriptException e) {
        // Script not in Redis cache - reload and retry
        logger.warning(
            CocoLogger.warning("Lua script SHA not found on Redis, reloading: " + scriptPath));
        String scriptContent = loadLuaScriptFromResource(scriptPath);
        sha = jedis.scriptLoad(scriptContent);
        luaScriptShas.put(scriptPath, sha);
        return jedis.evalsha(sha, keys, args);
      }
    } catch (Exception e) {
      logger.severe(
          CocoLogger.error("Lua script execution failed: " + scriptPath + " - " + e.getMessage()));
      throw new RuntimeException("Lua script error", e);
    }
  }

  /**
   * Atomic: Cache write + Pub/Sub broadcast (HSET + EXPIRE + PUBLISH).
   * Guarantees consistency - either all operations succeed or all fail.
   * 
   * @param hashName Redis hash name (e.g., "tan:query_cache")
   * @param channel Pub/Sub channel (e.g., "tan:sync:cache_invalidation")
   * @param cacheKey Hash field (e.g., "tan:cache:territory:abc123")
   * @param cacheValue JSON data to cache
   * @param ttlSeconds TTL for hash
   * @param pubSubMessage JSON message to broadcast
   * @return "OK" on success
   */
  public String atomicCachePublish(
      String hashName,
      String channel,
      String cacheKey,
      String cacheValue,
      int ttlSeconds,
      String pubSubMessage) {
    List<String> keys = List.of(hashName, channel);
    List<String> args = List.of(cacheKey, cacheValue, String.valueOf(ttlSeconds), pubSubMessage);
    return (String) evalLua("redis-lua/atomic_cache_publish.lua", keys, args);
  }

  /**
   * Atomic: Multi-key invalidation (HDEL).
   * Guarantees all cache keys are deleted together.
   * 
   * @param hashName Redis hash name (e.g., "tan:query_cache")
   * @param cacheKeys Array of cache keys to delete
   * @return Number of keys deleted
   */
  public long atomicMultiInvalidate(String hashName, String... cacheKeys) {
    List<String> keys = List.of(hashName);
    List<String> args = List.of(cacheKeys);
    return (Long) evalLua("redis-lua/atomic_multi_invalidate.lua", keys, args);
  }

  /**
   * Atomic: Multi-key invalidation + Pub/Sub broadcast (HDEL + PUBLISH).
   * Guarantees cache deletion and sync message are consistent.
   * 
   * @param hashName Redis hash name (e.g., "tan:query_cache")
   * @param channel Pub/Sub channel (e.g., "tan:sync:cache_invalidation")
   * @param pubSubMessage JSON message to broadcast
   * @param cacheKeys Array of cache keys to delete
   * @return Number of keys deleted
   */
  public long atomicInvalidatePublish(
      String hashName, String channel, String pubSubMessage, String... cacheKeys) {
    List<String> keys = List.of(hashName, channel);
    List<String> args = new ArrayList<>();
    args.add(pubSubMessage);
    for (String key : cacheKeys) {
      args.add(key);
    }
    return (Long) evalLua("redis-lua/atomic_invalidate_publish.lua", keys, args);
  }
}
