package org.tan.TownsAndNations.storage.Legacy;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.tan.TownsAndNations.DataClass.legacy.ClaimedChunk;
import org.tan.TownsAndNations.DataClass.RegionData;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.storage.DataStorage.RegionDataStorage;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;

import java.io.*;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.*;

import static org.tan.TownsAndNations.TownsAndNations.isSqlEnable;


public class ClaimedChunkStorage {
    private static Map<String, ClaimedChunk> claimedChunksMap = new HashMap<>();

    private static String getChunkKey(Chunk chunk) {
        return chunk.getX() + "," + chunk.getZ() + "," + chunk.getWorld().getUID();
    }

    public static Map<String, ClaimedChunk> getClaimedChunksMap() {
        return claimedChunksMap;
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
        try (FileWriter writer = new FileWriter(file, false);){
            gson.toJson(claimedChunksMap, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

