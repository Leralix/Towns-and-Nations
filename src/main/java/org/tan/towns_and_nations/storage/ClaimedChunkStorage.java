package org.tan.towns_and_nations.storage;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.tan.towns_and_nations.DataClass.ClaimedChunkDataClass;
import org.tan.towns_and_nations.DataClass.TownDataClass;
import org.tan.towns_and_nations.TownsAndNations;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;


public class ClaimedChunkStorage {
    private static Set<ClaimedChunkDataClass> claimedChunks = new HashSet<>();

    public static boolean isChunkClaimed(Chunk chunk) {
        return claimedChunks.contains(new ClaimedChunkDataClass(chunk));
    }

    public static String getChunkOwner(Chunk chunk) {
        return Objects.requireNonNull(getClaimedChunk(chunk)).getTownID();
    }
    public static String getChunkOwnerName(Chunk chunk) {
        return TownDataStorage.getTown(getClaimedChunk(chunk).getTownID()).getTownName();
    }

    public static boolean isOwner(Chunk chunk, String townID) {
        return claimedChunks.contains(new ClaimedChunkDataClass(chunk, townID));
    }

    public static void claimChunk(Chunk chunk, String townID) {
        claimedChunks.add(new ClaimedChunkDataClass(chunk, townID));
        saveStats();
    }

    public static void unclaimChunk(Chunk chunk) {
        claimedChunks.remove(new ClaimedChunkDataClass(chunk));
        saveStats();
    }

    public static ClaimedChunkDataClass getClaimedChunk(Chunk chunk){
        for (ClaimedChunkDataClass claimedChunk : claimedChunks){
            if(claimedChunk.equals(new ClaimedChunkDataClass(chunk))){
                return claimedChunk;
            }
        }
        return null;
    }


    public static void loadStats() {

        Gson gson = new Gson();
        File file = new File(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath() + "/TaNchunks.json");
        if (file.exists()){
            Reader reader = null;
            try {
                reader = new FileReader(file);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }

            Type type = new TypeToken<Set<ClaimedChunkDataClass>>() {}.getType();
            claimedChunks = gson.fromJson(reader, type);
        }

    }

    public static void saveStats() {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File file = new File(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath() + "/TaNchunks.json");
        file.getParentFile().mkdirs();
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (Writer writer = new FileWriter(file, false)) {
            gson.toJson(claimedChunks, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("[TaN]Claimed chunks saved");
    }


}

