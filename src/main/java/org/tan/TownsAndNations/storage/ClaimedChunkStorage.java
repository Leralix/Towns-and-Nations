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

    public static boolean isChunkClaimed(Chunk chunk) {
        return claimedChunksMap.containsKey(getChunkKey(chunk));
    }

    public static String getChunkOwnerID(Chunk chunk) {
        return claimedChunksMap.get(getChunkKey(chunk)).getTownID();
    }

    public static TownData getChunkOwnerTown(Chunk chunk) {
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

    public static void unclaimChunk(Chunk chunk) {
        claimedChunksMap.remove(getChunkKey(chunk));
        saveStats();
    }

    public static ClaimedChunk get(Chunk chunk) {
        return claimedChunksMap.get(getChunkKey(chunk));
    }


    public static void loadStats() {

        Gson gson = new Gson();
        File file = new File(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath() + "/TanChunks.json");
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
        File file = new File(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath() + "/TanChunks.json");
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

