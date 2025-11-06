package org.leralix.tan.storage.stored;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Chunk;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.chunk.*;
import org.leralix.tan.dataclass.territory.TerritoryData;

public class NewClaimedChunkStorage extends DatabaseStorage<ClaimedChunk2> {

  private static final String TABLE_NAME = "tan_claimed_chunks";
  private static NewClaimedChunkStorage instance;

  private NewClaimedChunkStorage() {
    super(TABLE_NAME, ClaimedChunk2.class, new GsonBuilder().setPrettyPrinting().create());
  }

  public static synchronized NewClaimedChunkStorage getInstance() {
    if (instance == null) {
      instance = new NewClaimedChunkStorage();
    }
    return instance;
  }

  /**
   * Get a chunk synchronously from cache (FAST - no DB blocking) This method checks the cache first
   * and returns immediately. If not in cache, returns null and triggers async load in background.
   *
   * <p>IMPORTANT: This method is designed for event listeners that cannot block.
   *
   * @param id The chunk ID
   * @return The chunk from cache, or null if not cached
   */
  public ClaimedChunk2 getFromCacheOrNull(String id) {
    if (id == null) {
      return null;
    }

    // Check cache only - no blocking!
    if (cacheEnabled && cache != null) {
      synchronized (cache) {
        ClaimedChunk2 cached = cache.get(id);
        if (cached != null) {
          return cached;
        }
      }
    }

    // Not in cache - trigger async load for next time (fire and forget)
    get(id)
        .thenAccept(
            chunk -> {
              // Chunk will be cached by get() method automatically
            });

    return null;
  }

  /**
   * @deprecated This method blocks the calling thread - avoid using in event listeners! Use
   *     getFromCacheOrNull() instead for non-blocking access.
   */
  @Deprecated
  public ClaimedChunk2 getSync(String id) {
    try {
      return get(id).join();
    } catch (Exception e) {
      TownsAndNations.getPlugin()
          .getLogger()
          .warning("Error getting claimed chunk data synchronously: " + e.getMessage());
      return null;
    }
  }

  @Override
  protected void createTable() {
    String createTableSQL =
        """
            CREATE TABLE IF NOT EXISTS %s (
                id VARCHAR(255) PRIMARY KEY,
                data TEXT NOT NULL
            )
        """
            .formatted(TABLE_NAME);

    try (Connection conn = getDatabase().getDataSource().getConnection();
        Statement stmt = conn.createStatement()) {
      stmt.execute(createTableSQL);
    } catch (SQLException e) {
      TownsAndNations.getPlugin()
          .getLogger()
          .severe("Error creating table " + TABLE_NAME + ": " + e.getMessage());
    }
  }

  @Override
  protected void createIndexes() {
    // PERFORMANCE FIX: Add index on ownerID for faster chunk lookups by territory
    // This significantly speeds up getAllChunkFrom() queries
    String createOwnerIndexSQL =
        "CREATE INDEX IF NOT EXISTS idx_chunk_owner ON "
            + TABLE_NAME
            + " ((CAST(JSON_EXTRACT(data, '$.ownerID') AS CHAR(255))))";

    try (Connection conn = getDatabase().getDataSource().getConnection();
        Statement stmt = conn.createStatement()) {
      stmt.execute(createOwnerIndexSQL);
      TownsAndNations.getPlugin()
          .getLogger()
          .info("Created index idx_chunk_owner on " + TABLE_NAME);
    } catch (SQLException e) {
      // If functional index fails (older MySQL versions), try regular column
      TownsAndNations.getPlugin()
          .getLogger()
          .warning(
              "Could not create functional index on "
                  + TABLE_NAME
                  + ", this is normal for older MySQL versions: "
                  + e.getMessage());
    }
  }

  private static String getChunkKey(Chunk chunk) {
    return getChunkKey(chunk.getX(), chunk.getZ(), chunk.getWorld().getUID().toString());
  }

  private static String getChunkKey(ClaimedChunk2 chunk) {
    return getChunkKey(chunk.getX(), chunk.getZ(), chunk.getWorldUUID());
  }

  private static String getChunkKey(int x, int z, String chunkWorldUID) {
    return x + "," + z + "," + chunkWorldUID;
  }

  public Map<String, ClaimedChunk2> getClaimedChunksMap() {
    return getAllAsync().join();
  }

  public boolean isChunkClaimed(Chunk chunk) {
    return exists(getChunkKey(chunk));
  }

  public Collection<TerritoryChunk> getAllChunkFrom(TerritoryData territoryData) {
    return getAllChunkFrom(territoryData.getID());
  }

  public Collection<TerritoryChunk> getAllChunkFrom(String territoryDataID) {
    List<TerritoryChunk> chunks = new ArrayList<>();

    // Optimized: filter in SQL using json_extract
    String selectSQL =
        "SELECT id, data FROM " + TABLE_NAME + " WHERE json_extract(data, '$.ownerID') = ?";

    try (Connection conn = getDatabase().getDataSource().getConnection();
        PreparedStatement ps = conn.prepareStatement(selectSQL)) {

      ps.setString(1, territoryDataID);

      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          String jsonData = rs.getString("data");
          ClaimedChunk2 chunk = deserializeChunk(jsonData);
          if (chunk instanceof TerritoryChunk territoryChunk) {
            chunks.add(territoryChunk);
          }
        }
      }
      return Collections.unmodifiableCollection(chunks);

    } catch (SQLException e) {
      TownsAndNations.getPlugin()
          .getLogger()
          .warning("Error optimized query, falling back to full scan: " + e.getMessage());

      // Fallback to old method
      for (ClaimedChunk2 chunk : getAllAsync().join().values()) {
        if (chunk instanceof TerritoryChunk territoryChunk
            && territoryChunk.getOwnerID().equals(territoryDataID)) {
          chunks.add(territoryChunk);
        }
      }
      return Collections.unmodifiableCollection(chunks);
    }
  }

  /** Deserialize chunk from JSON with proper type detection */
  private ClaimedChunk2 deserializeChunk(String jsonData) {
    JsonObject jsonObject = gson.fromJson(jsonData, JsonObject.class);
    JsonElement ownerIdElement = jsonObject.get("ownerID");

    if (ownerIdElement == null || ownerIdElement.isJsonNull()) {
      return null;
    }

    String ownerId = ownerIdElement.getAsString();

    if (ownerId.startsWith("T")) {
      return gson.fromJson(jsonData, TownClaimedChunk.class);
    } else if (ownerId.startsWith("R")) {
      return gson.fromJson(jsonData, RegionClaimedChunk.class);
    } else if (ownerId.startsWith("L")) {
      return gson.fromJson(jsonData, LandmarkClaimedChunk.class);
    }

    return null;
  }

  public TownClaimedChunk claimTownChunk(Chunk chunk, String ownerID) {
    TownClaimedChunk townClaimedChunk = new TownClaimedChunk(chunk, ownerID);
    putAsync(getChunkKey(chunk), townClaimedChunk).join();
    return townClaimedChunk;
  }

  public void claimRegionChunk(Chunk chunk, String ownerID) {
    putAsync(getChunkKey(chunk), new RegionClaimedChunk(chunk, ownerID)).join();
  }

  public void claimLandmarkChunk(Chunk chunk, String ownerID) {
    putAsync(getChunkKey(chunk), new LandmarkClaimedChunk(chunk, ownerID)).join();
  }

  /**
   * Check if all adjacent chunks are claimed by the same territory. This method uses batch loading
   * to avoid N+1 queries.
   *
   * @param chunk The center chunk
   * @param territoryID The territory ID to check
   * @return CompletableFuture with result
   */
  public CompletableFuture<Boolean> isAllAdjacentChunksClaimedBySameTerritoryAsync(
      Chunk chunk, String territoryID) {
    List<String> adjacentChunkKeys =
        Arrays.asList(
            getChunkKey(chunk.getX() + 1, chunk.getZ(), chunk.getWorld().getUID().toString()),
            getChunkKey(chunk.getX() - 1, chunk.getZ(), chunk.getWorld().getUID().toString()),
            getChunkKey(chunk.getX(), chunk.getZ() + 1, chunk.getWorld().getUID().toString()),
            getChunkKey(chunk.getX(), chunk.getZ() - 1, chunk.getWorld().getUID().toString()));

    // Batch load all adjacent chunks in parallel
    List<CompletableFuture<ClaimedChunk2>> futures =
        adjacentChunkKeys.stream().map(this::get).toList();

    return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
        .thenApply(
            v -> {
              for (CompletableFuture<ClaimedChunk2> future : futures) {
                ClaimedChunk2 adjacentClaimedChunk = future.join();

                if (adjacentClaimedChunk == null) {
                  return false;
                }

                if (adjacentClaimedChunk instanceof TerritoryChunk territoryChunk) {
                  if (!territoryChunk.getOccupierID().equals(territoryID)) {
                    return false;
                  }
                }
              }
              return true;
            });
  }

  /**
   * @deprecated This method blocks the thread - use
   *     isAllAdjacentChunksClaimedBySameTerritoryAsync() instead
   */
  @Deprecated
  public boolean isAllAdjacentChunksClaimedBySameTerritory(Chunk chunk, String territoryID) {
    try {
      return isAllAdjacentChunksClaimedBySameTerritoryAsync(chunk, territoryID).join();
    } catch (Exception e) {
      TownsAndNations.getPlugin()
          .getLogger()
          .warning("Error checking adjacent chunks: " + e.getMessage());
      return false;
    }
  }

  /**
   * Check if at least one adjacent chunk is claimed by the same territory. This method uses batch
   * loading to avoid N+1 queries.
   *
   * @param chunk The center chunk
   * @param townID The territory ID to check
   * @return CompletableFuture with result
   */
  public CompletableFuture<Boolean> isOneAdjacentChunkClaimedBySameTerritoryAsync(
      Chunk chunk, String townID) {
    List<String> adjacentChunkKeys =
        Arrays.asList(
            getChunkKey(chunk.getX() + 1, chunk.getZ(), chunk.getWorld().getUID().toString()),
            getChunkKey(chunk.getX() - 1, chunk.getZ(), chunk.getWorld().getUID().toString()),
            getChunkKey(chunk.getX(), chunk.getZ() + 1, chunk.getWorld().getUID().toString()),
            getChunkKey(chunk.getX(), chunk.getZ() - 1, chunk.getWorld().getUID().toString()));

    // Batch load all adjacent chunks in parallel
    List<CompletableFuture<ClaimedChunk2>> futures =
        adjacentChunkKeys.stream().map(this::get).toList();

    return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
        .thenApply(
            v -> {
              for (CompletableFuture<ClaimedChunk2> future : futures) {
                ClaimedChunk2 adjacentClaimedChunk = future.join();
                if (adjacentClaimedChunk != null
                    && adjacentClaimedChunk.getOwnerID().equals(townID)) {
                  return true;
                }
              }
              return false;
            });
  }

  /**
   * @deprecated This method blocks the thread - use isOneAdjacentChunkClaimedBySameTerritoryAsync()
   *     instead
   */
  @Deprecated
  public boolean isOneAdjacentChunkClaimedBySameTerritory(Chunk chunk, String townID) {
    try {
      return isOneAdjacentChunkClaimedBySameTerritoryAsync(chunk, townID).join();
    } catch (Exception e) {
      TownsAndNations.getPlugin()
          .getLogger()
          .warning("Error checking adjacent chunks: " + e.getMessage());
      return false;
    }
  }

  public void unclaimChunkAndUpdate(ClaimedChunk2 claimedChunk) {
    unclaimChunk(claimedChunk);
    claimedChunk.notifyUpdate();
  }

  public void unclaimChunk(ClaimedChunk2 claimedChunk) {
    deleteAsync(getChunkKey(claimedChunk)).join();
  }

  public void unclaimChunk(Chunk chunk) {
    unclaimChunk(get(chunk));
  }

  public @NotNull List<ClaimedChunk2> getFourAjacentChunks(ClaimedChunk2 chunk) {
    return Arrays.asList(
        get(chunk.getX(), chunk.getZ() - 1, chunk.getWorld().getUID().toString()), // NORTH
        get(chunk.getX() + 1, chunk.getZ(), chunk.getWorld().getUID().toString()), // EAST
        get(chunk.getX(), chunk.getZ() + 1, chunk.getWorld().getUID().toString()), // SOUTH
        get(chunk.getX() - 1, chunk.getZ(), chunk.getWorld().getUID().toString()) // WEST
        );
  }

  public @NotNull List<ClaimedChunk2> getEightAjacentChunks(ClaimedChunk2 chunk) {
    return Arrays.asList(
        get(chunk.getX(), chunk.getZ() - 1, chunk.getWorld().getUID().toString()), // Haut
        get(
            chunk.getX() + 1,
            chunk.getZ() - 1,
            chunk.getWorld().getUID().toString()), // Haut-droite
        get(chunk.getX() + 1, chunk.getZ(), chunk.getWorld().getUID().toString()), // Droite
        get(chunk.getX() + 1, chunk.getZ() + 1, chunk.getWorld().getUID().toString()), // Bas-droite
        get(chunk.getX(), chunk.getZ() + 1, chunk.getWorld().getUID().toString()), // Bas
        get(chunk.getX() - 1, chunk.getZ() + 1, chunk.getWorld().getUID().toString()), // Bas-gauche
        get(chunk.getX() - 1, chunk.getZ(), chunk.getWorld().getUID().toString()), // Gauche
        get(chunk.getX() - 1, chunk.getZ() - 1, chunk.getWorld().getUID().toString()) // Haut-gauche
        );
  }

  public void unclaimAllChunksFromTerritory(TerritoryData territoryData) {
    unclaimAllChunkFromID(territoryData.getID());
  }

  public void unclaimAllChunkFromID(String id) {
    // Optimized: batch delete using SQL
    String deleteSQL = "DELETE FROM " + TABLE_NAME + " WHERE json_extract(data, '$.ownerID') = ?";

    try (Connection conn = getDatabase().getDataSource().getConnection();
        PreparedStatement ps = conn.prepareStatement(deleteSQL)) {

      ps.setString(1, id);
      int deleted = ps.executeUpdate();

      TownsAndNations.getPlugin()
          .getLogger()
          .info("Deleted " + deleted + " chunks for territory " + id);

      // PERFORMANCE FIX: Invalidate only affected chunks instead of clearing entire cache
      invalidateCacheIf(chunk -> chunk.getOwnerID().equals(id));

    } catch (SQLException e) {
      TownsAndNations.getPlugin()
          .getLogger()
          .warning(
              "Error in optimized delete, falling back to individual deletes: " + e.getMessage());

      // Fallback to old method
      Map<String, ClaimedChunk2> allChunks = getAllAsync().join();
      List<String> toDelete = new ArrayList<>();
      for (Map.Entry<String, ClaimedChunk2> entry : allChunks.entrySet()) {
        ClaimedChunk2 chunk = entry.getValue();
        if (chunk.getOwnerID().equals(id)) {
          toDelete.add(entry.getKey());
        }
      }
      deleteAll(toDelete);
    }
  }

  /**
   * Get chunk from cache (non-blocking). Returns WildernessChunk if not claimed or not in cache.
   * IMPORTANT: For event listeners - this method never blocks!
   *
   * @param x Chunk X coordinate
   * @param z Chunk Z coordinate
   * @param worldID World UUID
   * @return The chunk (WildernessChunk if not claimed or not cached)
   */
  public ClaimedChunk2 get(int x, int z, String worldID) {
    ClaimedChunk2 claimedChunk = getFromCacheOrNull(getChunkKey(x, z, worldID));
    if (claimedChunk == null) {
      return new WildernessChunk(x, z, worldID);
    }
    return claimedChunk;
  }

  /**
   * Get chunk from cache (non-blocking). Returns WildernessChunk if not claimed or not in cache.
   * IMPORTANT: For event listeners - this method never blocks!
   *
   * @param chunk The Bukkit chunk
   * @return The chunk (WildernessChunk if not claimed or not cached)
   */
  public @NotNull ClaimedChunk2 get(Chunk chunk) {
    ClaimedChunk2 claimedChunk = getFromCacheOrNull(getChunkKey(chunk));
    if (claimedChunk == null) {
      return new WildernessChunk(chunk);
    }
    return claimedChunk;
  }

  /**
   * Preload chunks in a region into cache (async, non-blocking) Call this when loading a world
   * region to populate cache
   *
   * @param centerX Center chunk X
   * @param centerZ Center chunk Z
   * @param worldID World UUID
   * @param radius Radius in chunks
   * @return CompletableFuture that completes when preloading is done
   */
  public CompletableFuture<Void> preloadChunksAsync(
      int centerX, int centerZ, String worldID, int radius) {
    List<CompletableFuture<ClaimedChunk2>> futures = new ArrayList<>();

    for (int x = centerX - radius; x <= centerX + radius; x++) {
      for (int z = centerZ - radius; z <= centerZ + radius; z++) {
        futures.add(get(getChunkKey(x, z, worldID)));
      }
    }

    return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
  }

  @Override
  public void reset() {
    instance = null;
  }
}
