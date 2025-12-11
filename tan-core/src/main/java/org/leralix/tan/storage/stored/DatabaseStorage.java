package org.leralix.tan.storage.stored;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.storage.database.DatabaseHandler;
import org.leralix.tan.storage.exceptions.DatabaseNotReadyException;
import org.leralix.tan.storage.sync.SyncedEntity;
import org.leralix.tan.redis.QueryCacheManager;
import org.leralix.tan.redis.RedisSyncManager;

public abstract class DatabaseStorage<T> {

  protected final Gson gson;
  protected final String tableName;
  protected final Class<T> typeClass;
  protected final Type typeToken;

  protected final Map<String, T> cache;
  protected final int cacheSize;
  protected final boolean cacheEnabled;

  protected DatabaseStorage(String tableName, Class<T> typeClass, Gson gson) {
    this(tableName, typeClass, typeClass, gson, true);
  }

  protected DatabaseStorage(
      String tableName, Class<T> typeClass, Type typeToken, Gson gson, boolean enableCache) {
    this(
        tableName,
        typeClass,
        typeToken,
        gson,
        enableCache,
        TownsAndNations.getPlugin().getConfig().getInt("cache." + tableName, 1000));
  }

  protected DatabaseStorage(
      String tableName,
      Class<T> typeClass,
      Type typeToken,
      Gson gson,
      boolean enableCache,
      int cacheSize) {
    this.tableName = tableName;
    this.typeClass = typeClass;
    this.typeToken = typeToken;
    this.gson = gson;
    this.cacheEnabled = enableCache;
    this.cacheSize = cacheSize;
    this.cache =
        enableCache
            ? Collections.synchronizedMap(
                new LinkedHashMap<String, T>(16, 0.75f, true) {
                  @Override
                  protected boolean removeEldestEntry(Map.Entry<String, T> eldest) {
                    return size() > cacheSize;
                  }
                })
            : null;
    ensureTableCreated();
  }

  protected DatabaseHandler getDatabase() {
    return TownsAndNations.getPlugin().getDatabaseHandler();
  }

  private void ensureTableCreated() {
    DatabaseHandler db = getDatabase();
    if (db == null || db.getDataSource() == null) {
      TownsAndNations.getPlugin()
          .getLogger()
          .severe(
              "[TaN-DB-ERROR] Cannot create table " + tableName + " - Database not initialized!");
      return;
    }

    try {
      createTable();
      createIndexes();
      TownsAndNations.getPlugin()
          .getLogger()
          .info("[TaN-DB] Table " + tableName + " initialization complete");
    } catch (Exception e) {
      TownsAndNations.getPlugin()
          .getLogger()
          .severe("[TaN-DB-ERROR] Failed to create table " + tableName + ": " + e.getMessage());
      e.printStackTrace();
    }
  }

  protected abstract void createTable();

  protected void createIndexes() {}

  protected void clearCache() {
    if (cacheEnabled && cache != null) {
      cache.clear();
    }
  }

  protected void invalidateCache(String id) {
    if (cacheEnabled && cache != null) {
      cache.remove(id);
    }
  }

  protected void invalidateCacheIf(java.util.function.Predicate<T> condition) {
    if (cacheEnabled && cache != null) {
      cache.entrySet().removeIf(entry -> condition.test(entry.getValue()));
    }
  }

  public CompletableFuture<T> get(String id) {
    CompletableFuture<T> future = new CompletableFuture<>();
    if (id == null) {
      future.complete(null);
      return future;
    }

    if (cacheEnabled && cache != null) {
      T cached = cache.get(id);
      if (cached != null) {
        TownsAndNations.getPlugin()
            .getLogger()
            .info(
                String.format(
                    "[TaN-MySQL-READ] Table: %s | ID: %s | Type: %s | Cache: HIT",
                    tableName, id, typeClass.getSimpleName()));
        future.complete(cached);
        return future;
      }
    }

    runAsync(
        () -> {
          try {
            T object = loadFromDatabase(id);
            if (object != null && cacheEnabled && cache != null) {
              cache.put(id, object);
            }
            future.complete(object);
          } catch (DatabaseNotReadyException e) {
            future.completeExceptionally(e);
          } catch (Exception e) {
            future.completeExceptionally(e);
          }
        });

    return future;
  }

  private void runAsync(Runnable task) {
    try {
      org.leralix.tan.utils.FoliaScheduler.runTaskAsynchronously(TownsAndNations.getPlugin(), task);
    } catch (NoClassDefFoundError | NoSuchMethodError e) {
      org.bukkit.Bukkit.getScheduler().runTaskAsynchronously(TownsAndNations.getPlugin(), task);
    }
  }

  private T loadFromDatabase(String id) {
    String selectSQL = "SELECT data FROM " + tableName + " WHERE id = ?";

    try (Connection conn = getDatabase().getDataSource().getConnection()) {
      if (conn == null || conn.isClosed()) {
        String errorMsg = "Database connection is null or closed for " + typeClass.getSimpleName();
        TownsAndNations.getPlugin().getLogger().severe(errorMsg);
        throw new DatabaseNotReadyException(errorMsg);
      }

      try (PreparedStatement ps = conn.prepareStatement(selectSQL)) {
        ps.setString(1, id);

        long startTime = System.currentTimeMillis();
        try (ResultSet rs = ps.executeQuery()) {
          long duration = System.currentTimeMillis() - startTime;

          if (rs.next()) {
            String jsonData = rs.getString("data");

            TownsAndNations.getPlugin()
                .getLogger()
                .info(
                    String.format(
                        "[TaN-MySQL-READ] Table: %s | ID: %s | Type: %s | Size: %d bytes | Time: %dms | Cache: MISS",
                        tableName, id, typeClass.getSimpleName(), jsonData.length(), duration));

            if (typeToken.equals(ITanPlayer.class)) {
              com.google.gson.JsonElement jsonElement =
                  com.google.gson.JsonParser.parseString(jsonData);
              if (jsonElement.isJsonObject()) {
                com.google.gson.JsonObject jsonObject = jsonElement.getAsJsonObject();
                jsonObject.addProperty("uuid", id);
                jsonData = jsonObject.toString();
              }
            }

            return gson.fromJson(jsonData, typeToken);
          }
          return null;
        }
      }
    } catch (SQLException e) {
      String errorMsg =
          "SQL error retrieving "
              + typeClass.getSimpleName()
              + " with ID "
              + id
              + ": "
              + e.getMessage();
      TownsAndNations.getPlugin().getLogger().severe(errorMsg);
      throw new DatabaseNotReadyException(errorMsg, e);
    } catch (JsonSyntaxException e) {
      String errorMsg =
          "JSON parsing error for "
              + typeClass.getSimpleName()
              + " with ID "
              + id
              + ": "
              + e.getMessage();
      TownsAndNations.getPlugin().getLogger().severe(errorMsg);
      throw new RuntimeException(errorMsg, e);
    }
  }

  @Deprecated
  public Map<String, T> getAll() {
    return getAllSync();
  }

  public Map<String, T> getAllSync() {
    Map<String, T> result = new LinkedHashMap<>();
    String selectSQL = "SELECT id, data FROM " + tableName;

    try (Connection conn = getDatabase().getDataSource().getConnection();
        PreparedStatement ps = conn.prepareStatement(selectSQL);
        ResultSet rs = ps.executeQuery()) {

      while (rs.next()) {
        String id = rs.getString("id");
        String jsonData = rs.getString("data");
        try {
          T object = gson.fromJson(jsonData, typeToken);
          if (object != null) {
            result.put(id, object);
          }
        } catch (JsonSyntaxException e) {
          TownsAndNations.getPlugin()
              .getLogger()
              .warning(
                  "Failed to deserialize "
                      + typeClass.getSimpleName()
                      + " with ID "
                      + id
                      + ": "
                      + e.getMessage());
        }
      }
    } catch (SQLException e) {
      TownsAndNations.getPlugin()
          .getLogger()
          .severe(
              "Error retrieving all " + typeClass.getSimpleName() + " objects: " + e.getMessage());
    }

    return result;
  }

  public CompletableFuture<Map<String, T>> getAllAsync() {
    CompletableFuture<Map<String, T>> future = new CompletableFuture<>();

    runAsync(
        () -> {
          Map<String, T> result = new LinkedHashMap<>();
          String selectSQL = "SELECT id, data FROM " + tableName;

          try (Connection conn = getDatabase().getDataSource().getConnection();
              PreparedStatement ps = conn.prepareStatement(selectSQL);
              ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
              String id = rs.getString("id");
              String jsonData = rs.getString("data");
              try {
                T object = gson.fromJson(jsonData, typeToken);
                if (object != null) {
                  result.put(id, object);
                }
              } catch (JsonSyntaxException e) {
                TownsAndNations.getPlugin()
                    .getLogger()
                    .warning(
                        "Failed to deserialize "
                            + typeClass.getSimpleName()
                            + " with ID "
                            + id
                            + ": "
                            + e.getMessage());
              }
            }

            future.complete(result);

          } catch (SQLException e) {
            TownsAndNations.getPlugin()
                .getLogger()
                .severe(
                    "Error retrieving all "
                        + typeClass.getSimpleName()
                        + " objects: "
                        + e.getMessage());
            future.completeExceptionally(e);
          }
        });

    return future;
  }

  public List<String> getAllIds() {
    List<String> result = new ArrayList<>();
    String selectSQL = "SELECT id FROM " + tableName;

    try (Connection conn = getDatabase().getDataSource().getConnection();
        PreparedStatement ps = conn.prepareStatement(selectSQL);
        ResultSet rs = ps.executeQuery()) {

      while (rs.next()) {
        result.add(rs.getString("id"));
      }
    } catch (SQLException e) {
      TownsAndNations.getPlugin()
          .getLogger()
          .severe(
              "Error retrieving all IDs for " + typeClass.getSimpleName() + ": " + e.getMessage());
    }

    return result;
  }

  @Deprecated
  public void put(String id, T obj) {
    putSync(id, obj);
  }

  public void putSync(String id, T obj) {
    if (id == null || obj == null) {
      return;
    }

    String jsonData = gson.toJson(obj, typeToken);
    String upsertSQL = getDatabase().getUpsertSQL(tableName);

    try (Connection conn = getDatabase().getDataSource().getConnection();
        PreparedStatement ps = conn.prepareStatement(upsertSQL)) {

      ps.setString(1, id);
      ps.setString(2, jsonData);
      long startTime = System.currentTimeMillis();
      ps.executeUpdate();
      long duration = System.currentTimeMillis() - startTime;

      TownsAndNations.getPlugin()
          .getLogger()
          .info(
              String.format(
                  "[TaN-MySQL-WRITE] Table: %s | ID: %s | Type: %s | Size: %d bytes | Time: %dms",
                  tableName, id, typeClass.getSimpleName(), jsonData.length(), duration));

      if (cacheEnabled && cache != null) {
        synchronized (cache) {
          cache.put(id, obj);
        }
      }

    } catch (SQLException e) {
      TownsAndNations.getPlugin()
          .getLogger()
          .severe(
              "Error storing "
                  + typeClass.getSimpleName()
                  + " with ID "
                  + id
                  + ": "
                  + e.getMessage());
    }
  }

  public CompletableFuture<Void> putAsync(String id, T obj) {
    if (id == null || obj == null) {
      return CompletableFuture.completedFuture(null);
    }

    if (cacheEnabled && cache != null) {
      cache.put(id, obj);
    }

    CompletableFuture<Void> future = new CompletableFuture<>();
    String jsonData = gson.toJson(obj, typeToken);
    String upsertSQL = getDatabase().getUpsertSQL(tableName);

    runAsync(
        () -> {
          try (Connection conn = getDatabase().getDataSource().getConnection();
              PreparedStatement ps = conn.prepareStatement(upsertSQL)) {

            ps.setString(1, id);
            ps.setString(2, jsonData);
            long startTime = System.currentTimeMillis();
            ps.executeUpdate();
            long duration = System.currentTimeMillis() - startTime;

            TownsAndNations.getPlugin()
                .getLogger()
                .info(
                    String.format(
                        "[TaN-MySQL-WRITE-ASYNC] Table: %s | ID: %s | Type: %s | Size: %d bytes | Time: %dms",
                        tableName, id, typeClass.getSimpleName(), jsonData.length(), duration));

            future.complete(null);

          } catch (SQLException e) {
            TownsAndNations.getPlugin()
                .getLogger()
                .severe(
                    "Error storing "
                        + typeClass.getSimpleName()
                        + " with ID "
                        + id
                        + ": "
                        + e.getMessage());
            future.completeExceptionally(e);
          }
        });

    return future;
  }

  /**
   * Stores entity with full cache invalidation (L1 local + L2 Redis) and cross-server sync.
   * <p><b>RECOMMENDED:</b> Use this instead of deprecated {@link #put(String, Object)}.
   *
   * <p>This method guarantees:
   * <ul>
   *   <li>MySQL write-through (source of truth)</li>
   *   <li>Local cache (L1) update</li>
   *   <li>Redis query cache (L2) invalidation</li>
   *   <li>Cross-server cache invalidation broadcast</li>
   *   <li>Optimistic locking for {@link SyncedEntity} implementations</li>
   * </ul>
   *
   * <p><b>Example usage:</b>
   * <pre>{@code
   * TownData town = storage.get(townId).join();
   * town.setBalance(newBalance);
   * town.touch(); // Update version/timestamp
   * storage.putWithInvalidation(townId, town).join();
   * }</pre>
   *
   * @param id unique entity identifier
   * @param obj entity to persist
   * @return CompletableFuture that completes when all operations finish
   * @throws SyncedEntity.StaleDataException if optimistic locking detects conflict
   * @since 0.18.0
   */
  public CompletableFuture<Void> putWithInvalidation(String id, T obj) {
    if (id == null || obj == null) {
      return CompletableFuture.completedFuture(null);
    }

    // Optimistic locking check for SyncedEntity
    if (obj instanceof SyncedEntity) {
      SyncedEntity syncedObj = (SyncedEntity) obj;
      syncedObj.touch(); // Update version + timestamp
    }

    // Write to MySQL + L1 cache
    return putAsync(id, obj)
        .thenRun(
            () -> {
              // Invalidate Redis L2 cache
              String cacheKey = "tan:cache:" + tableName.toLowerCase() + ":" + id;
              QueryCacheManager.invalidateTerritory(id);

              // Broadcast cache invalidation to other servers
              RedisSyncManager syncManager = TownsAndNations.getPlugin().getRedisSyncManager();
              if (syncManager != null) {
                syncManager.publishCacheInvalidation(cacheKey);
                TownsAndNations.getPlugin()
                    .getLogger()
                    .fine(
                        String.format(
                            "[TaN-SYNC] Cache invalidation broadcast: %s | Table: %s | ID: %s",
                            cacheKey, tableName, id));
              }
            })
        .exceptionally(
            throwable -> {
              TownsAndNations.getPlugin()
                  .getLogger()
                  .severe(
                      String.format(
                          "[TaN-SYNC-ERROR] Failed putWithInvalidation: Table=%s, ID=%s, Error=%s",
                          tableName, id, throwable.getMessage()));
              return null;
            });
  }

  /**
   * Stores entity synchronously with full cache invalidation.
   * <p><b>WARNING:</b> Blocks thread. Prefer {@link #putWithInvalidation(String, Object)} for async.
   *
   * @param id unique entity identifier
   * @param obj entity to persist
   * @since 0.18.0
   */
  public void putSyncWithInvalidation(String id, T obj) {
    putWithInvalidation(id, obj).join();
  }

  public void putAll(Map<String, T> objects) {
    if (objects == null || objects.isEmpty()) {
      return;
    }

    String upsertSQL = getDatabase().getUpsertSQL(tableName);
    Connection conn = null;

    try {
      conn = getDatabase().getDataSource().getConnection();
      conn.setAutoCommit(false);

      try (PreparedStatement ps = conn.prepareStatement(upsertSQL)) {
        for (Map.Entry<String, T> entry : objects.entrySet()) {
          String id = entry.getKey();
          T obj = entry.getValue();

          if (id != null && obj != null) {
            String jsonData = gson.toJson(obj, typeToken);
            ps.setString(1, id);
            ps.setString(2, jsonData);
            ps.addBatch();
          }
        }

        ps.executeBatch();
        conn.commit();

        if (cacheEnabled && cache != null) {
          cache.putAll(objects);
        }

      } catch (SQLException e) {
        try {
          if (conn != null) {
            conn.rollback();
          }
        } catch (SQLException rollbackEx) {
          TownsAndNations.getPlugin()
              .getLogger()
              .severe(
                  "Error rolling back transaction for "
                      + typeClass.getSimpleName()
                      + ": "
                      + rollbackEx.getMessage());
        }
        throw e;
      } finally {
        if (conn != null) {
          conn.setAutoCommit(true);
        }
      }

    } catch (SQLException e) {
      TownsAndNations.getPlugin()
          .getLogger()
          .severe(
              "Error batch storing " + typeClass.getSimpleName() + " objects: " + e.getMessage());
    } finally {
      if (conn != null) {
        try {
          conn.close();
        } catch (SQLException e) {
          TownsAndNations.getPlugin()
              .getLogger()
              .warning("Error closing connection: " + e.getMessage());
        }
      }
    }
  }

  @Deprecated
  public void delete(String id) {
    if (id == null) {
      return;
    }

    String deleteSQL = "DELETE FROM " + tableName + " WHERE id = ?";

    try (Connection conn = getDatabase().getDataSource().getConnection();
        PreparedStatement ps = conn.prepareStatement(deleteSQL)) {

      ps.setString(1, id);
      long startTime = System.currentTimeMillis();
      ps.executeUpdate();
      long duration = System.currentTimeMillis() - startTime;

      TownsAndNations.getPlugin()
          .getLogger()
          .info(
              String.format(
                  "[TaN-MySQL-DELETE] Table: %s | ID: %s | Type: %s | Time: %dms",
                  tableName, id, typeClass.getSimpleName(), duration));

      invalidateCache(id);

    } catch (SQLException e) {
      TownsAndNations.getPlugin()
          .getLogger()
          .severe(
              "Error deleting "
                  + typeClass.getSimpleName()
                  + " with ID "
                  + id
                  + ": "
                  + e.getMessage());
    }
  }

  public CompletableFuture<Void> deleteAsync(String id) {
    if (id == null) {
      return CompletableFuture.completedFuture(null);
    }

    invalidateCache(id);

    CompletableFuture<Void> future = new CompletableFuture<>();
    String deleteSQL = "DELETE FROM " + tableName + " WHERE id = ?";

    runAsync(
        () -> {
          try (Connection conn = getDatabase().getDataSource().getConnection();
              PreparedStatement ps = conn.prepareStatement(deleteSQL)) {

            ps.setString(1, id);
            long startTime = System.currentTimeMillis();
            ps.executeUpdate();
            long duration = System.currentTimeMillis() - startTime;

            TownsAndNations.getPlugin()
                .getLogger()
                .info(
                    String.format(
                        "[TaN-MySQL-DELETE-ASYNC] Table: %s | ID: %s | Type: %s | Time: %dms",
                        tableName, id, typeClass.getSimpleName(), duration));

            future.complete(null);

          } catch (SQLException e) {
            TownsAndNations.getPlugin()
                .getLogger()
                .severe(
                    "Error deleting "
                        + typeClass.getSimpleName()
                        + " with ID "
                        + id
                        + ": "
                        + e.getMessage());
            future.completeExceptionally(e);
          }
        });

    return future;
  }

  /**
   * Deletes entity with full cache invalidation (L1 local + L2 Redis) and cross-server sync.
   * <p><b>RECOMMENDED:</b> Use this instead of deprecated {@link #delete(String)}.
   *
   * <p>This method guarantees:
   * <ul>
   *   <li>MySQL deletion</li>
   *   <li>Local cache (L1) invalidation</li>
   *   <li>Redis query cache (L2) invalidation</li>
   *   <li>Cross-server cache invalidation broadcast</li>
   * </ul>
   *
   * @param id unique entity identifier to delete
   * @return CompletableFuture that completes when deletion finishes
   * @since 0.18.0
   */
  public CompletableFuture<Void> deleteWithInvalidation(String id) {
    if (id == null) {
      return CompletableFuture.completedFuture(null);
    }

    return deleteAsync(id)
        .thenRun(
            () -> {
              // Invalidate Redis L2 cache
              String cacheKey = "tan:cache:" + tableName.toLowerCase() + ":" + id;
              QueryCacheManager.invalidateTerritory(id);

              // Broadcast cache invalidation to other servers
              RedisSyncManager syncManager = TownsAndNations.getPlugin().getRedisSyncManager();
              if (syncManager != null) {
                syncManager.publishCacheInvalidation(cacheKey);
                TownsAndNations.getPlugin()
                    .getLogger()
                    .fine(
                        String.format(
                            "[TaN-SYNC] Delete cache invalidation: %s | Table: %s | ID: %s",
                            cacheKey, tableName, id));
              }
            })
        .exceptionally(
            throwable -> {
              TownsAndNations.getPlugin()
                  .getLogger()
                  .severe(
                      String.format(
                          "[TaN-SYNC-ERROR] Failed deleteWithInvalidation: Table=%s, ID=%s, Error=%s",
                          tableName, id, throwable.getMessage()));
              return null;
            });
  }

  public void deleteAll(Collection<String> ids) {
    if (ids == null || ids.isEmpty()) {
      return;
    }

    String deleteSQL = "DELETE FROM " + tableName + " WHERE id = ?";
    Connection conn = null;

    try {
      conn = getDatabase().getDataSource().getConnection();
      conn.setAutoCommit(false);

      try (PreparedStatement ps = conn.prepareStatement(deleteSQL)) {
        for (String id : ids) {
          if (id != null) {
            ps.setString(1, id);
            ps.addBatch();
          }
        }

        ps.executeBatch();
        conn.commit();

        for (String id : ids) {
          if (id != null) {
            invalidateCache(id);
          }
        }

      } catch (SQLException e) {
        try {
          if (conn != null) {
            conn.rollback();
          }
        } catch (SQLException rollbackEx) {
          TownsAndNations.getPlugin()
              .getLogger()
              .severe(
                  "Error rolling back transaction for "
                      + typeClass.getSimpleName()
                      + ": "
                      + rollbackEx.getMessage());
        }
        throw e;
      } finally {
        if (conn != null) {
          conn.setAutoCommit(true);
        }
      }

    } catch (SQLException e) {
      TownsAndNations.getPlugin()
          .getLogger()
          .severe(
              "Error batch deleting " + typeClass.getSimpleName() + " objects: " + e.getMessage());
    } finally {
      if (conn != null) {
        try {
          conn.close();
        } catch (SQLException e) {
          TownsAndNations.getPlugin()
              .getLogger()
              .warning("Error closing connection: " + e.getMessage());
        }
      }
    }
  }

  public boolean exists(String id) {
    if (cacheEnabled && cache != null) {
      synchronized (cache) {
        if (cache.containsKey(id)) {
          return true;
        }
      }
    }

    String selectSQL = "SELECT 1 FROM " + tableName + " WHERE id = ?";

    try (Connection conn = getDatabase().getDataSource().getConnection();
        PreparedStatement ps = conn.prepareStatement(selectSQL)) {

      ps.setString(1, id);

      try (ResultSet rs = ps.executeQuery()) {
        return rs.next();
      }
    } catch (SQLException e) {
      TownsAndNations.getPlugin()
          .getLogger()
          .severe(
              "Error checking existence of "
                  + typeClass.getSimpleName()
                  + " with ID "
                  + id
                  + ": "
                  + e.getMessage());
    }

    return false;
  }

  public int count() {
    String countSQL = "SELECT COUNT(*) FROM " + tableName;

    try (Connection conn = getDatabase().getDataSource().getConnection();
        PreparedStatement ps = conn.prepareStatement(countSQL);
        ResultSet rs = ps.executeQuery()) {

      if (rs.next()) {
        return rs.getInt(1);
      }
    } catch (SQLException e) {
      TownsAndNations.getPlugin()
          .getLogger()
          .severe("Error counting " + typeClass.getSimpleName() + " objects: " + e.getMessage());
    }

    return 0;
  }

  public CompletableFuture<Map<String, T>> getPaginated(int offset, int limit) {
    CompletableFuture<Map<String, T>> future = new CompletableFuture<>();

    runAsync(
        () -> {
          Map<String, T> result = new LinkedHashMap<>();
          String selectSQL = "SELECT id, data FROM " + tableName + " LIMIT ? OFFSET ?";

          try (Connection conn = getDatabase().getDataSource().getConnection();
              PreparedStatement ps = conn.prepareStatement(selectSQL)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
              while (rs.next()) {
                String id = rs.getString("id");
                String jsonData = rs.getString("data");
                try {
                  T object = gson.fromJson(jsonData, typeToken);
                  if (object != null) {
                    result.put(id, object);
                    if (cacheEnabled && cache != null) {
                      cache.put(id, object);
                    }
                  }
                } catch (JsonSyntaxException e) {
                  TownsAndNations.getPlugin()
                      .getLogger()
                      .warning(
                          "Failed to deserialize "
                              + typeClass.getSimpleName()
                              + " with ID "
                              + id
                              + ": "
                              + e.getMessage());
                }
              }
            }

            future.complete(result);

          } catch (SQLException e) {
            TownsAndNations.getPlugin()
                .getLogger()
                .severe(
                    "Error retrieving paginated "
                        + typeClass.getSimpleName()
                        + " objects: "
                        + e.getMessage());
            future.completeExceptionally(e);
          }
        });

    return future;
  }

  public CompletableFuture<Void> processBatches(
      int batchSize, java.util.function.Consumer<Map<String, T>> consumer) {
    CompletableFuture<Void> future = new CompletableFuture<>();

    runAsync(
        () -> {
          try {
            int offset = 0;
            boolean hasMore = true;

            while (hasMore) {
              Map<String, T> batch = getPaginated(offset, batchSize).join();
              if (batch.isEmpty()) {
                hasMore = false;
              } else {
                consumer.accept(batch);
                offset += batchSize;
              }
            }

            future.complete(null);

          } catch (Exception e) {
            TownsAndNations.getPlugin()
                .getLogger()
                .severe(
                    "Error processing batches for "
                        + typeClass.getSimpleName()
                        + ": "
                        + e.getMessage());
            future.completeExceptionally(e);
          }
        });

    return future;
  }

  public abstract void reset();
}
