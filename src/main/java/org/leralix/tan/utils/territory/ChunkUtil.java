package org.leralix.tan.utils.territory;

import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.chunk.TerritoryChunk;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ChunkUtil {

    private static final NewClaimedChunkStorage claimedChunkStorage = NewClaimedChunkStorage.getInstance();

    private ChunkUtil(){
        throw new AssertionError("Utility class");
    }

    public static boolean isChunkEncirecledBy(ClaimedChunk2 center, Predicate<ClaimedChunk2> predicate) {
        for (ClaimedChunk2 neighbor : claimedChunkStorage.getAjacentChunks(center)) {
            if (!predicate.test(neighbor)) {
                return false;
            }
        }
        return true;
    }

    public static List<ClaimedChunk2> getBorderChunks(TerritoryData territoryData) {

        List<ClaimedChunk2> res = new ArrayList<>();

        for(TerritoryChunk territoryChunk : NewClaimedChunkStorage.getInstance().getAllChunkFrom(territoryData)) {
            if (!isChunkEncirecledBy(territoryChunk, chunk -> territoryData.getID().equals(chunk.getOwnerID()))) {
                res.add(territoryChunk);
            }
        }

        return res;

    }
}
