package org.tan.TownsAndNations.API;

import org.tan.TownsAndNations.DataClass.ClaimedChunk;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.storage.ClaimedChunkStorage;
import org.tan.TownsAndNations.storage.TownDataStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class tanAPI {


    public static String getAPIVersion(){
        return "0.0.1";
    }

    public HashMap<String, TownData> getTownList(){
        return TownDataStorage.getTownList();
    }

    public Map<String, ClaimedChunk> getChunkMap(){
        return ClaimedChunkStorage.getClaimedChunksMap();
    }

    public Collection<ClaimedChunk> getChunkList(){
        return ClaimedChunkStorage.getClaimedChunksMap().values();
    }



}
