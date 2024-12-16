package org.leralix.tan.api;

import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;

import java.util.Collection;
import java.util.Map;

public class TanApi {

    static String version = "1.0.0";

    public Map<String, TownData> getTownList(){
        return TownDataStorage.getTownMap();
    }

    public Map<String, ClaimedChunk2> getChunkMap(){
        return NewClaimedChunkStorage.getClaimedChunksMap();
    }

    public Collection<ClaimedChunk2> getChunkList(){
        return NewClaimedChunkStorage.getClaimedChunksMap().values();
    }

    public int getChunkColor(String id){
        if(id.startsWith("T")){
            return TownDataStorage.get(id).getChunkColorCode();
        }
        else if(id.startsWith("R")){
            return RegionDataStorage.get(id).getChunkColorCode();
        }
        return 0x000000;
    }
    public int getChunkColor(ClaimedChunk2 chunk){
        return getChunkColor(chunk.getOwnerID());
    }

}
