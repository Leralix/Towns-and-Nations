package org.leralix.tan.storage.stored;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.storage.database.DatabaseHandler;

import java.lang.reflect.Type;
import java.sql.*;
import java.util.*;

/**
 * Base class for database-backed storage with optional caching.
 * Each get() retrieves data from the database or cache, each set() writes to the database.
 * Includes LRU cache for frequently accessed data to reduce DB queries.
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

    protected DatabaseStorage(String tableName, Class<T> typeClass, Type typeToken, Gson gson, boolean enableCache) {
        this(tableName, typeClass, typeToken, gson, enableCache, TownsAndNations.getPlugin().getConfig().getInt("cache." + tableName, 100));
    }

    protected DatabaseStorage(String tableName, Class<T> typeClass, Type typeToken, Gson gson, boolean enableCache, int cacheSize) {
        this.tableName = tableName;
        this.typeClass = typeClass;
        this.typeToken = typeToken;
        this.gson = gson;
        this.cacheEnabled = enableCache;
        this.cacheSize = cacheSize;
        this.cache = enableCache ? new LinkedHashMap<String, T>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, T> eldest) {
                return size() > cacheSize;
            }
        } : null;
        createTable();
        createIndexes();
    }

    /**
     * Get the database handler
     */
    protected DatabaseHandler getDatabase() {
        return TownsAndNations.getPlugin().getDatabaseHandler();
    }

    /**
     * Create the table if it doesn't exist
     */
    protected abstract void createTable();

    /**
     * Create indexes for better performance
     * Override this method to add custom indexes
     */
    protected void createIndexes() {
        // Default: create index on id (usually already primary key, but good for lookups)
        // Subclasses can override to add more indexes
    }

    /**
     * Clear the cache
     */
    protected void clearCache() {
        if (cacheEnabled && cache != null) {
            synchronized (cache) {
                cache.clear();
            }
        }
    }

    /**
     * Remove an entry from cache
     */
    protected void invalidateCache(String id) {
        if (cacheEnabled && cache != null) {
            synchronized (cache) {
                cache.remove(id);
            }
        }
    }

    /**
     * Get an object by ID from the database or cache
     * @param id The ID of the object
     * @return The object, or null if not found
     */
    public T get(String id) {
        if (id == null) {
            return null;
        }

        // Check cache first (with double-checked locking pattern for better performance)
        if (cacheEnabled && cache != null) {
            T cached = cache.get(id);
            if (cached != null) {
                return cached;
            }

            // Synchronize on the cache to prevent multiple threads from loading the same data
            synchronized (cache) {
                // Double-check: another thread might have loaded it while we were waiting
                cached = cache.get(id);
                if (cached != null) {
                    return cached;
                }

                // Load from database and cache it
                T object = loadFromDatabase(id);
                if (object != null) {
                    cache.put(id, object);
                }
                return object;
            }
        }

        // Cache disabled, just load from database
        return loadFromDatabase(id);
    }

    /**
     * Load an object from the database (internal method, not cached)
     * @param id The ID of the object
     * @return The object, or null if not found
     */
    private T loadFromDatabase(String id) {
        String selectSQL = "SELECT data FROM " + tableName + " WHERE id = ?";

        try (Connection conn = getDatabase().getDataSource().getConnection()) {
            // Validate connection
            if (conn == null || conn.isClosed()) {
                TownsAndNations.getPlugin().getLogger().severe(
                    "Database connection is null or closed for " + typeClass.getSimpleName()
                );
                return null;
            }

            try (PreparedStatement ps = conn.prepareStatement(selectSQL)) {
                ps.setString(1, id);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String jsonData = rs.getString("data");

                        if (typeToken.equals(ITanPlayer.class)) {
                            com.google.gson.JsonElement jsonElement = com.google.gson.JsonParser.parseString(jsonData);
                            if (jsonElement.isJsonObject()) {
                                com.google.gson.JsonObject jsonObject = jsonElement.getAsJsonObject();
                                jsonObject.addProperty("uuid", id);
                                jsonData = jsonObject.toString();
                            }
                        }

                        return gson.fromJson(jsonData, typeToken);
                    }
                }
            }
        } catch (SQLException | JsonSyntaxException e) {
            TownsAndNations.getPlugin().getLogger().severe(
                "Error retrieving " + typeClass.getSimpleName() + " with ID " + id + ": " + e.getMessage()
            );
        }

        return null;
    }

    /**
     * Get all objects from the database
     * WARNING: This can be expensive for large tables. Consider using pagination or specific queries.
     * @return A map of ID to object
     */
    public Map<String, T> getAll() {
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
                    TownsAndNations.getPlugin().getLogger().warning(
                        "Failed to deserialize " + typeClass.getSimpleName() + " with ID " + id + ": " + e.getMessage()
                    );
                }
            }
        } catch (SQLException e) {
            TownsAndNations.getPlugin().getLogger().severe(
                "Error retrieving all " + typeClass.getSimpleName() + " objects: " + e.getMessage()
            );
        }

        return result;
    }

    /**
     * Get all IDs from the database (faster than getAll() when you only need IDs)
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
            TownsAndNations.getPlugin().getLogger().severe(
                "Error retrieving all IDs for " + typeClass.getSimpleName() + ": " + e.getMessage()
            );
        }

        return result;
    }

    /**
     * Put an object in the database
     * @param id The ID of the object
     * @param obj The object to store
     */
    public void put(String id, T obj) {
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
            TownsAndNations.getPlugin().getLogger().severe(
                "Error storing " + typeClass.getSimpleName() + " with ID " + id + ": " + e.getMessage()
            );
        }
    }

    /**
     * Batch insert/update multiple objects (more efficient than multiple put() calls)
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
                    synchronized (cache) {
                        cache.putAll(objects);
                    }
                }

            } catch (SQLException e) {
                try {
                    if (conn != null) {
                        conn.rollback();
                    }
                } catch (SQLException rollbackEx) {
                    TownsAndNations.getPlugin().getLogger().severe(
                        "Error rolling back transaction for " + typeClass.getSimpleName() + ": " + rollbackEx.getMessage()
                    );
                }
                throw e;
            } finally {
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            }

        } catch (SQLException e) {
            TownsAndNations.getPlugin().getLogger().severe(
                "Error batch storing " + typeClass.getSimpleName() + " objects: " + e.getMessage()
            );
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    TownsAndNations.getPlugin().getLogger().warning(
                        "Error closing connection: " + e.getMessage()
                    );
                }
            }
        }
    }

    /**
     * Delete an object from the database
     * @param id The ID of the object
     */
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
            TownsAndNations.getPlugin().getLogger().severe(
                "Error deleting " + typeClass.getSimpleName() + " with ID " + id + ": " + e.getMessage()
            );
        }
    }

    /**
     * Batch delete multiple objects (more efficient than multiple delete() calls)
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
                    TownsAndNations.getPlugin().getLogger().severe(
                        "Error rolling back transaction for " + typeClass.getSimpleName() + ": " + rollbackEx.getMessage()
                    );
                }
                throw e;
            } finally {
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            }

        } catch (SQLException e) {
            TownsAndNations.getPlugin().getLogger().severe(
                "Error batch deleting " + typeClass.getSimpleName() + " objects: " + e.getMessage()
            );
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    TownsAndNations.getPlugin().getLogger().warning(
                        "Error closing connection: " + e.getMessage()
                    );
                }
            }
        }
    }

    /**
     * Check if an ID exists in the database
     * @param id The ID to check
     * @return true if exists, false otherwise
     */
    public boolean exists(String id) {
        String selectSQL = "SELECT 1 FROM " + tableName + " WHERE id = ?";

        try (Connection conn = getDatabase().getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(selectSQL)) {

            ps.setString(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            TownsAndNations.getPlugin().getLogger().severe(
                "Error checking existence of " + typeClass.getSimpleName() + " with ID " + id + ": " + e.getMessage()
            );
        }

        return false;
    }

    /**
     * Get the count of objects in the database
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
            TownsAndNations.getPlugin().getLogger().severe(
                "Error counting " + typeClass.getSimpleName() + " objects: " + e.getMessage()
            );
        }

        return 0;
    }

    /**
     * Reset the storage (for testing purposes)
     */
    public abstract void reset();
}
