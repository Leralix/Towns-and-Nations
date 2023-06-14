package org.tan.towns_and_nations.storage;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.tan.towns_and_nations.DataClass.ClaimedChunkDataClass;

import java.util.Objects;
import java.util.Set;
import java.util.HashSet;


public class ClaimedChunkStorage {
    private static final Set<ClaimedChunkDataClass> claimedChunks = new HashSet<>();

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
    }

    public static void unclaimChunk(Chunk chunk) {
        claimedChunks.remove(new ClaimedChunkDataClass(chunk));
    }

    public static ClaimedChunkDataClass getClaimedChunk(Chunk chunk){
        for (ClaimedChunkDataClass claimedChunk : claimedChunks){
            if(claimedChunk.equals(new ClaimedChunkDataClass(chunk))){
                return claimedChunk;
            }
        }
        return null;
    }
}

