package org.tan.TownsAndNations.utils;

import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.DataClass.TownRank;
import org.tan.TownsAndNations.DataClass.legacy.ClaimedChunk;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.storage.DataStorage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.Legacy.ClaimedChunkStorage;
import org.tan.TownsAndNations.storage.DataStorage.NewClaimedChunkStorage;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class UpdateUtil {

    public static void update(){
        updateNewChunkData();
    }




    public static void updateNewChunkData() {
        if(ClaimedChunkStorage.getClaimedChunksMap() == null)
            return;

        Map<String, ClaimedChunk> newMap = ClaimedChunkStorage.getClaimedChunksMap();

        if (newMap.isEmpty()) {
            TownsAndNations.getPluginLogger().info("-No claimed chunks to update");
            return;
        }
        Iterator<Map.Entry<String, ClaimedChunk>> iterator = ClaimedChunkStorage.getClaimedChunksMap().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ClaimedChunk> entry = iterator.next();
            ClaimedChunk oldClaimedChunk = entry.getValue();
            NewClaimedChunkStorage.claimTownChunk(oldClaimedChunk.getChunk(), oldClaimedChunk.getID());
            iterator.remove();
        }
        TownsAndNations.getPlugin().getLogger().info("-Claimed chunks have been updated to the new system");
    }

}
