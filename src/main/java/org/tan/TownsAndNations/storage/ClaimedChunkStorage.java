package org.tan.TownsAndNations.storage;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.tan.TownsAndNations.DataClass.ClaimedChunk;
import org.tan.TownsAndNations.DataClass.RegionData;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.TownsAndNations;

import java.io.*;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.*;

import static org.tan.TownsAndNations.TownsAndNations.isSqlEnable;


public class ClaimedChunkStorage {
    private static Map<String, ClaimedChunk> claimedChunksMap = new HashMap<String, ClaimedChunk>();
    private static Connection connection;

    private static String getChunkKey(Chunk chunk) {
        return chunk.getX() + "," + chunk.getZ() + "," + chunk.getWorld().getUID();
    }
    private static String getChunkKey(int x, int z, String chunkWorldUID){
        return x + "," + z + "," + chunkWorldUID;
    }

    public static Map<String, ClaimedChunk> getClaimedChunksMap() {
        return claimedChunksMap;
    }

    public static boolean isChunkClaimed(Chunk chunk) {
        return claimedChunksMap.containsKey(getChunkKey(chunk));
    }

    public static String getChunkOwnerID(Chunk chunk) {
        if (isSqlEnable()) {
            return getChunkOwnerIDFromDatabase(chunk);
        } else {
            ClaimedChunk claimedChunk = claimedChunksMap.get(getChunkKey(chunk));
            return claimedChunk != null ? claimedChunk.getID() : null;
        }
    }

    private static String getChunkOwnerIDFromDatabase(Chunk chunk) {
        String chunkKey = getChunkKey(chunk);
        String sql = "SELECT town_id FROM claimed_chunks WHERE chunk_key = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, chunkKey);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("town_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static TownData getChunkOwnerTown(Chunk chunk) {
        if (isSqlEnable()) {
            return getChunkOwnerTownFromDatabase(chunk);
        } else {
            if (!isChunkClaimed(chunk)) {
                return null;
            }
            return TownDataStorage.get(ClaimedChunkStorage.getChunkOwnerID(chunk));
        }
    }

    private static TownData getChunkOwnerTownFromDatabase(Chunk chunk) {
        String chunkKey = getChunkKey(chunk);
        String sql = "SELECT town_id FROM claimed_chunks WHERE chunk_key = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, chunkKey);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String townID = rs.getString("town_id");
                    return TownDataStorage.get(townID); // Supposant que TownDataStorage.get peut gérer les données de la base de données
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getChunkOwnerName(Chunk chunk) {
        if (isSqlEnable()) {
            return getChunkOwnerNameFromDatabase(chunk);
        } else {
            TownData townData = ClaimedChunkStorage.getChunkOwnerTown(chunk);
            if (townData != null) {
                return townData.getName();
            }
            return null;
        }
    }

    private static String getChunkOwnerNameFromDatabase(Chunk chunk) {
        String chunkKey = getChunkKey(chunk);
        String sql = "SELECT t.name FROM claimed_chunks c " +
                "JOIN tan_town_data t ON c.town_id = t.town_key " +
                "WHERE c.chunk_key = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, chunkKey);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("name");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isOwner(Chunk chunk, String townID) {
        if (isSqlEnable()) {
            return isOwnerInDatabase(chunk, townID);
        } else {
            ClaimedChunk cChunk = claimedChunksMap.get(ClaimedChunkStorage.getChunkKey(chunk));
            return cChunk != null && cChunk.getID().equals(townID);
        }
    }

    private static boolean isOwnerInDatabase(Chunk chunk, String townID) {
        String chunkKey = getChunkKey(chunk);
        String sql = "SELECT town_id FROM claimed_chunks WHERE chunk_key = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, chunkKey);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return townID.equals(rs.getString("town_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void claimChunk(Chunk chunk, String ownerID) {
        if (isSqlEnable()) {
            claimChunkInDatabase(chunk, ownerID);
        } else {
            claimedChunksMap.put(ClaimedChunkStorage.getChunkKey(chunk), new ClaimedChunk(chunk, ownerID));
            ClaimedChunkStorage.saveStats();
        }
    }

    private static void claimChunkInDatabase(Chunk chunk, String townID) {
        String chunkKey = getChunkKey(chunk);
        String sql = "INSERT INTO claimed_chunks (chunk_key, x, z, world_id, town_id) VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE town_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, chunkKey);
            ps.setInt(2, chunk.getX());
            ps.setInt(3, chunk.getZ());
            ps.setString(4, chunk.getWorld().getUID().toString());
            ps.setString(5, townID);
            ps.setString(6, townID); // Pour la clause ON DUPLICATE KEY UPDATE
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean isAdjacentChunkClaimedBySameTown(Chunk chunk, String townID) {
        if (isSqlEnable()) {
            // Si le stockage en base de données est activé, effectuer la vérification en base de données
            return checkAdjacentChunksInDatabase(chunk, townID);
        } else {
            // Sinon, utiliser la logique existante avec claimedChunksMap
            return checkAdjacentChunksInMap(chunk, townID);
        }
    }

    private static boolean checkAdjacentChunksInDatabase(Chunk chunk, String townID) {
        // Liste des clés des chunks adjacents
        List<String> adjacentChunkKeys = Arrays.asList(
                getChunkKey(chunk.getX() + 1, chunk.getZ(), chunk.getWorld().getUID().toString()),
                getChunkKey(chunk.getX() - 1, chunk.getZ(), chunk.getWorld().getUID().toString()),
                getChunkKey(chunk.getX(), chunk.getZ() + 1, chunk.getWorld().getUID().toString()),
                getChunkKey(chunk.getX(), chunk.getZ() - 1, chunk.getWorld().getUID().toString())
        );

        String sql = "SELECT town_id FROM claimed_chunks WHERE chunk_key = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (String key : adjacentChunkKeys) {
                ps.setString(1, key);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && townID.equals(rs.getString("town_id"))) {
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean checkAdjacentChunksInMap(Chunk chunk, String townID) {

        List<String> adjacentChunkKeys = Arrays.asList(
                getChunkKey(chunk.getX() + 1, chunk.getZ(), chunk.getWorld().getUID().toString()),
                getChunkKey(chunk.getX() - 1, chunk.getZ(), chunk.getWorld().getUID().toString()),
                getChunkKey(chunk.getX(), chunk.getZ() + 1, chunk.getWorld().getUID().toString()),
                getChunkKey(chunk.getX(), chunk.getZ() - 1, chunk.getWorld().getUID().toString())
        );

        for (String adjacentChunkKey : adjacentChunkKeys) {
            ClaimedChunk adjacentClaimedChunk = claimedChunksMap.get(adjacentChunkKey);
            if (adjacentClaimedChunk != null && adjacentClaimedChunk.getID().equals(townID)) {
                return true;
            }
        }

        return false;
    }

    public static void unclaimChunk(Chunk chunk) {
        if (isSqlEnable()) {
            unclaimChunkInDatabase(chunk);
        } else {
            claimedChunksMap.remove(getChunkKey(chunk));
            saveStats();
        }
    }

    private static void unclaimChunkInDatabase(Chunk chunk) {
        String chunkKey = getChunkKey(chunk);
        String sql = "DELETE FROM claimed_chunks WHERE chunk_key = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, chunkKey);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void unclaimAllChunkFromTown(String townID) {;
        String sql = "DELETE FROM claimed_chunks WHERE town_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, townID);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void unclaimAllChunkFrom(String townID) {
        for (ClaimedChunk chunk : claimedChunksMap.values()) {
            if (chunk.getID().equals(townID))
                unclaimChunk(Bukkit.getWorld(UUID.fromString(chunk.getWorldUUID())).getChunkAt(chunk.getX(), chunk.getZ()));
        }
    }

    public static ClaimedChunk get(Chunk chunk) {
        return claimedChunksMap.get(ClaimedChunkStorage.getChunkKey(chunk));
    }

    public static void loadStats() {
        Gson gson = new Gson();
        File file = new File(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath() + "/TAN - Chunks.json");
        if (file.exists()) {
            FileReader reader = null;
            try {
                reader = new FileReader(file);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            Type type = new TypeToken<Map<String, ClaimedChunk>>(){}.getType();
            claimedChunksMap = (Map)gson.fromJson((Reader)reader, type);
        }
    }

    public static void saveStats() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File file = new File(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath() + "/TAN - Chunks.json");
        file.getParentFile().mkdirs();
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (FileWriter writer = new FileWriter(file, false);){
            gson.toJson(claimedChunksMap, (Appendable)writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void initialize(String host, String username, String password) {
        try {
            connection = DriverManager.getConnection(host, username, password);
            try (Statement statement = connection.createStatement()){
                String sql = "CREATE TABLE IF NOT EXISTS claimed_chunks (chunk_key VARCHAR(255) PRIMARY KEY,x INT,z INT,world_id VARCHAR(255),town_id VARCHAR(255))";
                statement.executeUpdate(sql);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean isChunkClaimedByTownRegion(TownData townData, Chunk chunkToClaim) {

        ClaimedChunk claimedChunk = claimedChunksMap.get(getChunkKey(chunkToClaim));

        if(claimedChunk.isRegion()){
            RegionData regionData = RegionDataStorage.get(claimedChunk.getID());
            return regionData.isTownInRegion(townData);
        }
        return false;

    }
}

