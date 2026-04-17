package org.leralix.tan.storage.stored.json;

import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import org.bukkit.Chunk;
import org.leralix.tan.data.chunk.*;
import org.leralix.tan.storage.stored.ClaimStorage;
import org.leralix.tan.storage.typeadapter.ClaimedChunkDeserializer;
import org.leralix.tan.utils.territory.ChunkUtil;

import java.util.Collection;
import java.util.HashMap;

public class NewClaimedChunkStorage extends JsonStorage<IClaimedChunk> implements ClaimStorage {

    public NewClaimedChunkStorage() {
        super("TAN - Claimed Chunks.json",
                new TypeToken<HashMap<String, ChunkData>>() {
                }.getType(),
                new GsonBuilder()
                        .setPrettyPrinting()
                        .registerTypeAdapter(ChunkData.class, new ClaimedChunkDeserializer())
                        .create());
    }

    @Override
    public boolean isChunkClaimed(String chunkKey){
        return dataMap.containsKey(chunkKey);
    }

    @Override
    public TownClaimedChunk claimTownChunk(Chunk chunk, String ownerID) {
        TownClaimedChunk townClaimedChunk = new TownClaimedChunk(chunk, ownerID);
        dataMap.put(ChunkUtil.getChunkKey(chunk), townClaimedChunk);
        return townClaimedChunk;
    }

    @Override
    public RegionClaimedChunk claimRegionChunk(Chunk chunk, String ownerID) {
        RegionClaimedChunk regionClaimedChunk = new RegionClaimedChunk(chunk, ownerID);
        dataMap.put(ChunkUtil.getChunkKey(chunk), regionClaimedChunk);
        return regionClaimedChunk;
    }

    @Override
    public NationClaimedChunk claimNationChunk(Chunk chunk, String ownerID) {
        NationClaimedChunk nationClaimedChunk = new NationClaimedChunk(chunk, ownerID);
        dataMap.put(ChunkUtil.getChunkKey(chunk), nationClaimedChunk);
        return nationClaimedChunk;
    }

    @Override
    public void claimLandmarkChunk(Chunk chunk, String ownerID) {
        dataMap.put(ChunkUtil.getChunkKey(chunk), new LandmarkClaimedChunk(chunk, ownerID));
    }

    @Override
    public Collection<IClaimedChunk> getAllChunks() {
        return dataMap.values();
    }

    @Override
    public void unclaimChunk(IClaimedChunk claimedChunk) {
        dataMap.remove(ChunkUtil.getChunkKey(claimedChunk));
    }
}
