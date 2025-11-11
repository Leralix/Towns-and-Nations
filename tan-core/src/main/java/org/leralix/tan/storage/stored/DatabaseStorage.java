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

/**
 * Base class for database-backed storage with optional caching. Each get() retrieves data from the
 * database or cache, each set() writes to the database. Includes LRU cache for frequently accessed
 * data to reduce DB queries.
 *
 * @param <T> The type of object being stored
 */
public abstract class DatabaseStorage<T> {

  protected final Gson gson;
  protected final String tableName;
  protected final Class<T> typeClass;
  protected final Type typeToken;

  // Optional LRU cache for frequently accessed objects (max 100 entries)
  protected final Map<String, T> cache;
  protected final int cacheSize;
  protected final boolean cacheEnabled;

  protected DatabaseStorage(String tableName, Class<T> typeClass, Gson gson) {
    this(tableName, typeClass, typeClass, gson, true);
  }

  protected DatabaseStorage(
      String tableName, Class<T> typeClass, Type typeToken, Gson gson, boolean enableCache) {
    // PERFORMANCE FIX: Default cache size increased from 100 to 1000
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
    // PERFORMANCE FIX: Increased default from 100 to 1000 for better cache hit rate on busy servers
    // For large servers, configure higher in config.yml: cache.<table_name>: 5000
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
    createTable();
    createIndexes();
  }

  /** Get the database handler */
  protected DatabaseHandler getDatabase() {
    return TownsAndNations.getPlugin().getDatabaseHandler();
  }

  /** Create the table if it doesn't exist */
  protected abstract void createTable();

  /** Create indexes for better performance Override this method to add custom indexes */
  protected void createIndexes() {
    // Default: create index on id (usually already primary key, but good for lookups)
    // Subclasses can override to add more indexes
  }

  /** Clear the cache */
  protected void clearCache() {
    if (cacheEnabled && cache != null) {
      cache.clear();
    }
  }

  /** Remove an entry from cache */
  protected void invalidateCache(String id) {
    if (cacheEnabled && cache != null) {
      cache.remove(id);
    }
  }

  /**
   * Invalidate cache entries by matching a condition (e.g., by owner ID) More efficient than
   * clearing entire cache
   */
  protected void invalidateCacheIf(java.util.function.Predicate<T> condition) {
    if (cacheEnabled && cache != null) {
      cache.entrySet().removeIf(entry -> condition.test(entry.getValue()));
    }
  }

  /**
   * Get an object by ID from the database or cache
   *
   * @param id The ID of the object
   * @return The object, or null if not found
   */
  public CompletableFuture<T> get(String id) {
    CompletableFuture<T> future = new CompletableFuture<>();
    if (id == null) {
      future.complete(null);
      return future;
    }

    // Check cache first
    if (cacheEnabled && cache != null) {
      T cached = cache.get(id);
      if (cached != null) {
        future.complete(cached);
        return future;
      }
    }

    // Load from database asynchronously
    runAsync(
        () -> {
          try {
            T object = loadFromDatabase(id);
            if (object != null && cacheEnabled && cache != null) {
              cache.put(id, object);
            }
            future.complete(object);
          } catch (DatabaseNotReadyException e) {
            // Propagate exception - caller should handle retry logic
            future.completeExceptionally(e);
          } catch (Exception e) {
            // Any other exception - propagate it
            future.completeExceptionally(e);
          }
        });

    return future;
  }

  /** Helper method to run a task asynchronously, detecting Folia/Paper environment. */
  private void runAsync(Runnable task) {
    try {
      // Check if FoliaScheduler is available (e.g., by trying to call a Folia-specific method)
      // This is a placeholder for actual Folia detection logic.
      // For now, we'll just use FoliaScheduler directly as it's already imported in other files.
      org.leralix.tan.utils.FoliaScheduler.runTaskAsynchronously(TownsAndNations.getPlugin(), task);
    } catch (NoClassDefFoundError | NoSuchMethodError e) {
      // Fallback for Paper/Spigot
      org.bukkit.Bukkit.getScheduler().runTaskAsynchronously(TownsAndNations.getPlugin(), task);
    }
  }

  /**
   * Load an object from the database (internal method, not cached)
   *
   * @param id The ID of the object
   * @return The object if found, null if not found in database
   * @throws DatabaseNotReadyException if database connection is not available (recoverable error -
   *     retry recommended)
   */
  private T loadFromDatabase(String id) {
    String selectSQL = "SELECT data FROM " + tableName + " WHERE id = ?";

    try (Connection conn = getDatabase().getDataSource().getConnection()) {
      // Validate connection - throw exception instead of returning null
      if (conn == null || conn.isClosed()) {
        String errorMsg = "Database connection is null or closed for " + typeClass.getSimpleName();
        TownsAndNations.getPlugin().getLogger().severe(errorMsg);
        throw new DatabaseNotReadyException(errorMsg);
      }

      try (PreparedStatement ps = conn.prepareStatement(selectSQL)) {
        ps.setString(1, id);

        try (ResultSet rs = ps.executeQuery()) {
          if (rs.next()) {
            String jsonData = rs.getString("data");

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
          // Player not found in database - return null (not an error, just not found)
          return null;
        }
      }
    } catch (SQLException e) {
      // SQL errors might indicate temporary database issues - throw DatabaseNotReadyException
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
      // JSON parsing errors indicate corrupted data - this is NOT recoverable
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

  /**
   * Get all objects from the database synchronously WARNING: This can be expensive for large
   * tables. Consider using pagination or specific queries.
   *
   * @deprecated Use getAllAsync() for background operations or getAllSync() for explicit
   *     synchronous needs
   * @return A map of ID to object
   */
  @Deprecated
  public Map<String, T> getAll() {
    return getAllSync();
  }

  /**
   * Get all objects from the database synchronously (blocks current thread) Use this method when
   * you explicitly need synchronous access (e.g., in GUI menus). For background operations, prefer
   * getAllAsync() or processBatches(). WARNING: This can be expensive for large tables.
   *
   * @return A map of ID to object
   */
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

  /**
   * Get all objects from the database asynchronously (non-blocking) WARNING: This can be expensive
   * for large tables. Consider using pagination or specific queries.
   *
   * @return CompletableFuture with a map of ID to object
   */
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

  /**
   * Get all IDs from the database (faster than getAll() when you only need IDs)
   *
   * @return A list of IDs
   */
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

  /**
   * Put an object in the database synchronously (blocks current thread)
   *
   * @deprecated Use putAsync() for background operations or putSync() for explicit synchronous
   *     needs
   * @param id The ID of the object
   * @param obj The object to store
   */
  @Deprecated
  public void put(String id, T obj) {
    putSync(id, obj);
  }

  /**
   * Put an object in the database synchronously (blocks current thread) Use this method when you
   * explicitly need synchronous write (e.g., critical saves). For most operations, prefer
   * putAsync() for better performance.
   *
   * @param id The ID of the object
   * @param obj The object to store
   */
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
      ps.executeUpdate();

      // Update cache
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

  /**
   * Put an object in the database asynchronously (non-blocking)
   *
   * @param id The ID of the object
   * @param obj The object to store
   * @return CompletableFuture that completes when the operation is done
   */
  public CompletableFuture<Void> putAsync(String id, T obj) {
    if (id == null || obj == null) {
      return CompletableFuture.completedFuture(null);
    }

    // Update cache immediately (optimistic update)
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
            ps.executeUpdate();

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
   * Batch insert/update multiple objects (more efficient than multiple put() calls)
   *
   * @param objects Map of ID to object
   */
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

        // Update cache
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

  /**
   * Delete an object from the database synchronously (blocks current thread)
   *
   * @deprecated Use deleteAsync() instead for non-blocking operations
   * @param id The ID of the object
   */
  @Deprecated
  public void delete(String id) {
    if (id == null) {
      return;
    }

    String deleteSQL = "DELETE FROM " + tableName + " WHERE id = ?";

    try (Connection conn = getDatabase().getDataSource().getConnection();
        PreparedStatement ps = conn.prepareStatement(deleteSQL)) {

      ps.setString(1, id);
      ps.executeUpdate();

      // Remove from cache
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

  /**
   * Delete an object from the database asynchronously (non-blocking)
   *
   * @param id The ID of the object
   * @return CompletableFuture that completes when the operation is done
   */
  public CompletableFuture<Void> deleteAsync(String id) {
    if (id == null) {
      return CompletableFuture.completedFuture(null);
    }

    // Remove from cache immediately (optimistic delete)
    invalidateCache(id);

    CompletableFuture<Void> future = new CompletableFuture<>();
    String deleteSQL = "DELETE FROM " + tableName + " WHERE id = ?";

    runAsync(
        () -> {
          try (Connection conn = getDatabase().getDataSource().getConnection();
              PreparedStatement ps = conn.prepareStatement(deleteSQL)) {

            ps.setString(1, id);
            ps.executeUpdate();

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
   * Batch delete multiple objects (more efficient than multiple delete() calls)
   *
   * @param ids List of IDs to delete
   */
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

        // Invalidate cache only after successful commit
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

  /**
   * Check if an ID exists in the database
   *
   * @param id The ID to check
   * @return true if exists, false otherwise
   */
  public boolean exists(String id) {
    // OPTIMIZATION: Check cache first
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

  /**
   * Get the count of objects in the database
   *
   * @return The count
   */
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

  /**
   * Get a paginated list of objects from the database (async) PERFORMANCE: Use this instead of
   * getAll() for large tables to avoid loading everything at once
   *
   * @param offset Starting position (0-indexed)
   * @param limit Maximum number of objects to retrieve
   * @return CompletableFuture with a map of ID to object
   */
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
                    // Update cache
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

  /**
   * Process all objects in batches using a consumer function PERFORMANCE: Use this for processing
   * large tables to avoid memory issues
   *
   * @param batchSize Number of objects to process per batch
   * @param consumer Function to process each batch
   * @return CompletableFuture that completes when all batches are processed
   */
  public CompletableFuture<Void> processBatches(
      int batchSize, java.util.function.Consumer<Map<String, T>> consumer) {
    CompletableFuture<Void> future = new CompletableFuture<>();

    runAsync(
        () -> {
          try {
            int offset = 0;
            boolean hasMore = true;

            // Process batches without counting total (avoids blocking count() call)
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

  /** Reset the storage (for testing purposes) */
  public abstract void reset();
}
