package org.leralix.tan.storage.stored;

import com.google.gson.GsonBuilder;
import org.bukkit.Chunk;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.chunk.*;
import org.leralix.tan.dataclass.territory.TerritoryData;

import java.sql.*;
import java.util.*;

public class NewClaimedChunkStorage extends DatabaseStorage<ClaimedChunk2>{

    private static final String TABLE_NAME = "tan_claimed_chunks";
    private static NewClaimedChunkStorage instance;

    private NewClaimedChunkStorage() {
        super(TABLE_NAME,
                ClaimedChunk2.class,
                new GsonBuilder()
                        .setPrettyPrinting()
                        .create());
    }

    public static synchronized NewClaimedChunkStorage getInstance() {
        if (instance == null) {
            instance = new NewClaimedChunkStorage();
        }
        return instance;
    }

    @Override
    protected void createTable() {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS %s (
                id VARCHAR(255) PRIMARY KEY,
                data TEXT NOT NULL
            )
        """.formatted(TABLE_NAME);

        try (Connection conn = getDatabase().getDataSource().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            TownsAndNations.getPlugin().getLogger().severe(
                "Error creating table " + TABLE_NAME + ": " + e.getMessage()
            );
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
        return getAll();
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
        String selectSQL = "SELECT id, data FROM " + TABLE_NAME + " WHERE json_extract(data, '$.ownerID') = ?";

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
            TownsAndNations.getPlugin().getLogger().warning(
                "Error optimized query, falling back to full scan: " + e.getMessage()
            );

            // Fallback to old method
            for (ClaimedChunk2 chunk : getAll().values()) {
                if (chunk instanceof TerritoryChunk territoryChunk && territoryChunk.getOwnerID().equals(territoryDataID)) {
                    chunks.add(territoryChunk);
                }
            }
            return Collections.unmodifiableCollection(chunks);
        }
    }

    /**
     * Deserialize chunk from JSON with proper type detection
     */
    private ClaimedChunk2 deserializeChunk(String jsonData) {
        // Parse JSON to detect type
        if (jsonData.contains("\"ownerID\":\"T")) {
            return gson.fromJson(jsonData, TownClaimedChunk.class);
        } else if (jsonData.contains("\"ownerID\":\"R")) {
            return gson.fromJson(jsonData, RegionClaimedChunk.class);
        } else if (jsonData.contains("\"ownerID\":\"L")) {
            return gson.fromJson(jsonData, LandmarkClaimedChunk.class);
        }
        return gson.fromJson(jsonData, ClaimedChunk2.class);
    }

    public TownClaimedChunk claimTownChunk(Chunk chunk, String ownerID) {
        TownClaimedChunk townClaimedChunk = new TownClaimedChunk(chunk, ownerID);
        put(getChunkKey(chunk), townClaimedChunk);
        return townClaimedChunk;
    }

    public void claimRegionChunk(Chunk chunk, String ownerID) {
        put(getChunkKey(chunk), new RegionClaimedChunk(chunk, ownerID));
    }

    public void claimLandmarkChunk(Chunk chunk, String ownerID) {
        put(getChunkKey(chunk), new LandmarkClaimedChunk(chunk, ownerID));
    }

    public boolean isAllAdjacentChunksClaimedBySameTerritory(Chunk chunk, String territoryID) {
        List<String> adjacentChunkKeys = Arrays.asList(
                getChunkKey(chunk.getX() + 1, chunk.getZ(), chunk.getWorld().getUID().toString()),
                getChunkKey(chunk.getX() - 1, chunk.getZ(), chunk.getWorld().getUID().toString()),
                getChunkKey(chunk.getX(), chunk.getZ() + 1, chunk.getWorld().getUID().toString()),
                getChunkKey(chunk.getX(), chunk.getZ() - 1, chunk.getWorld().getUID().toString())
        );

        for (String adjacentChunkKey : adjacentChunkKeys) {
            ClaimedChunk2 adjacentClaimedChunk = get(adjacentChunkKey);

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
    }

    public boolean isOneAdjacentChunkClaimedBySameTerritory(Chunk chunk, String townID) {

        List<String> adjacentChunkKeys = Arrays.asList(
                getChunkKey(chunk.getX() + 1, chunk.getZ(), chunk.getWorld().getUID().toString()),
                getChunkKey(chunk.getX() - 1, chunk.getZ(), chunk.getWorld().getUID().toString()),
                getChunkKey(chunk.getX(), chunk.getZ() + 1, chunk.getWorld().getUID().toString()),
                getChunkKey(chunk.getX(), chunk.getZ() - 1, chunk.getWorld().getUID().toString())
        );

        for (String adjacentChunkKey : adjacentChunkKeys) {
            ClaimedChunk2 adjacentClaimedChunk = get(adjacentChunkKey);
            if (adjacentClaimedChunk != null && adjacentClaimedChunk.getOwnerID().equals(townID)) {
                return true;
            }
        }
        return false;
    }

    public void unclaimChunkAndUpdate(ClaimedChunk2 claimedChunk) {
        unclaimChunk(claimedChunk);
        claimedChunk.notifyUpdate();
    }

    public void unclaimChunk(ClaimedChunk2 claimedChunk) {
        delete(getChunkKey(claimedChunk));
    }

    public void unclaimChunk(Chunk chunk) {
        unclaimChunk(get(chunk));
    }


    public @NotNull List<ClaimedChunk2> getFourAjacentChunks(ClaimedChunk2 chunk) {
        return Arrays.asList(
                get(chunk.getX(), chunk.getZ() - 1, chunk.getWorld().getUID().toString()), // NORTH
                get(chunk.getX() + 1, chunk.getZ(), chunk.getWorld().getUID().toString()), // EAST
                get(chunk.getX(), chunk.getZ() + 1, chunk.getWorld().getUID().toString()), // SOUTH
                get(chunk.getX() - 1, chunk.getZ(), chunk.getWorld().getUID().toString())  // WEST
        );
    }

    public @NotNull List<ClaimedChunk2> getEightAjacentChunks(ClaimedChunk2 chunk) {
        return Arrays.asList(
                get(chunk.getX(), chunk.getZ() - 1, chunk.getWorld().getUID().toString()), // Haut
                get(chunk.getX() + 1, chunk.getZ() - 1, chunk.getWorld().getUID().toString()), // Haut-droite
                get(chunk.getX() + 1, chunk.getZ(), chunk.getWorld().getUID().toString()), // Droite
                get(chunk.getX() + 1, chunk.getZ() + 1, chunk.getWorld().getUID().toString()), // Bas-droite
                get(chunk.getX(), chunk.getZ() + 1, chunk.getWorld().getUID().toString()), // Bas
                get(chunk.getX() - 1, chunk.getZ() + 1, chunk.getWorld().getUID().toString()), // Bas-gauche
                get(chunk.getX() - 1, chunk.getZ(), chunk.getWorld().getUID().toString()), // Gauche
                get(chunk.getX() - 1, chunk.getZ() - 1, chunk.getWorld().getUID().toString())  // Haut-gauche
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

            TownsAndNations.getPlugin().getLogger().info(
                "Deleted " + deleted + " chunks for territory " + id
            );

            // Clear cache for this territory (invalidate all since we don't have specific IDs)
            clearCache();

        } catch (SQLException e) {
            TownsAndNations.getPlugin().getLogger().warning(
                "Error in optimized delete, falling back to individual deletes: " + e.getMessage()
            );

            // Fallback to old method
            Map<String, ClaimedChunk2> allChunks = getAll();
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

    public ClaimedChunk2 get(int x, int z, String worldID) {
        ClaimedChunk2 claimedChunk = get(getChunkKey(x, z, worldID));
        if (claimedChunk == null) {
            return new WildernessChunk(x, z, worldID);
        }
        return claimedChunk;
    }

    public @NotNull ClaimedChunk2 get(Chunk chunk) {
        ClaimedChunk2 claimedChunk = get(getChunkKey(chunk));
        if (claimedChunk == null) {
            return new WildernessChunk(chunk);
        }
        return claimedChunk;
    }

    @Override
    public void reset() {
        instance = null;
    }
}
