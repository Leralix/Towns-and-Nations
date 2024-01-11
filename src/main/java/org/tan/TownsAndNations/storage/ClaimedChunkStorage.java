package org.tan.TownsAndNations.storage;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Chunk;
import org.tan.TownsAndNations.DataClass.ClaimedChunk;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.TownsAndNations;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;


public class ClaimedChunkStorage {
    private static Map<String, ClaimedChunk> claimedChunksMap = new HashMap<>();

    private static String getChunkKey(Chunk chunk) {
        return chunk.getX() + "," + chunk.getZ() + "," + chunk.getWorld().getUID();
    }

    public static Map<String, ClaimedChunk> getClaimedChunksMap() {
        return claimedChunksMap;
    }

    public static boolean isChunkClaimed(Chunk chunk) {
        return claimedChunksMap.containsKey(getChunkKey(chunk));
    }

    public static String getChunkOwnerID(Chunk chunk) {
        return claimedChunksMap.get(getChunkKey(chunk)).getTownID();
    }

    public static TownData getChunkOwnerTown(Chunk chunk) {
        if(!isChunkClaimed(chunk)){
            return null;
        }
        return TownDataStorage.get(getChunkOwnerID(chunk));
    }

    public static String getChunkOwnerName(Chunk chunk) {
        return getChunkOwnerTown(chunk).getName();
    }

    public static boolean isOwner(Chunk chunk, String townID) {
        ClaimedChunk cChunk = claimedChunksMap.get(getChunkKey(chunk));
        return cChunk != null && cChunk.getTownID().equals(townID);
    }

    public static void claimChunk(Chunk chunk, String townID) {
        claimedChunksMap.put(getChunkKey(chunk), new ClaimedChunk(chunk, townID));
        saveStats();
    }

    public static boolean isAdjacentChunkClaimedBySameTown(Chunk chunk, String townID) {
        String originalChunkKey = getChunkKey(chunk);

        List<String> adjacentChunkKeys = Arrays.asList(
                (chunk.getX() + 1) + "," + chunk.getZ() + "," + chunk.getWorld().getUID(),
                (chunk.getX() - 1) + "," + chunk.getZ() + "," + chunk.getWorld().getUID(),
                chunk.getX() + "," + (chunk.getZ() + 1) + "," + chunk.getWorld().getUID(),
                chunk.getX() + "," + (chunk.getZ() - 1) + "," + chunk.getWorld().getUID()
        );

        for (String adjacentChunkKey : adjacentChunkKeys) {
            ClaimedChunk adjacentClaimedChunk = claimedChunksMap.get(adjacentChunkKey);
            if (adjacentClaimedChunk != null && adjacentClaimedChunk.getTownID().equals(townID)) {
                return true;
            }
        }

        return false;
    }



    public static void unclaimChunk(Chunk chunk) {
        claimedChunksMap.remove(getChunkKey(chunk));
        saveStats();
    }

    public static void unclaimAllChunkFrom(String townID) {

        for (Map.Entry<String, ClaimedChunk> entry : claimedChunksMap.entrySet()) {
            ClaimedChunk value = entry.getValue();
            String key = entry.getKey();

            if (value.getTownID().equals(townID)){
                claimedChunksMap.remove(key);
            }

        }

    }

    public static ClaimedChunk get(Chunk chunk) {
        return claimedChunksMap.get(getChunkKey(chunk));
    }


    public static void loadStats() {

        Gson gson = new Gson();
        File file = new File(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath() + "/TAN - Chunks.json");
        if (file.exists()){
            Reader reader = null;
            try {
                reader = new FileReader(file);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }

            Type type = new TypeToken<Map<String, ClaimedChunk>>() {}.getType();
            claimedChunksMap = gson.fromJson(reader, type);
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
        try (Writer writer = new FileWriter(file, false)) {
            gson.toJson(claimedChunksMap, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}

