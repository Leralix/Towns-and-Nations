package org.leralix.tan.storage.stored.database;

import org.bukkit.Chunk;
import org.leralix.tan.data.chunk.*;
import org.leralix.tan.storage.stored.ClaimStorage;
import org.leralix.tan.utils.constants.database.RedisConfig;
import org.leralix.tan.utils.territory.ChunkUtil;

import java.util.ArrayList;
import java.util.Collection;

public class ClaimChunkDatabaseStorage extends DatabaseStorage<ChunkDatabase, ChunkData> implements ClaimStorage {

    public ClaimChunkDatabaseStorage(RedisConfig redisConfig) {
        super(new ChunkDbManager(redisConfig));
    }

    @Override
    public IClaimedChunk get(String chunkID) {
        return getOrLoad(chunkID, this::load);
    }

    @Override
    public boolean isChunkClaimed(String chunkKey) {
        var chunk = get(chunkKey);
        if(chunk == null){
            return false;
        }
        return chunk.isClaimed();
    }

    @Override
    public Collection<IClaimedChunk> getAllChunks() {
        return new ArrayList<>(databaseManager.getAll());
    }

    @Override
    public void unclaimChunk(IClaimedChunk claimedChunk) {
        databaseManager.delete(ChunkUtil.getChunkKey(claimedChunk));
    }

    @Override
    public void claimLandmarkChunk(Chunk chunk, String ownerID) {
        databaseManager.save(new LandmarkClaimedChunk(chunk, ownerID));
    }

    @Override
    public TownClaimedChunk claimTownChunk(Chunk chunk, String id) {
        TownClaimedChunk townClaimedChunk = new TownClaimedChunk(chunk, id);
        databaseManager.save(townClaimedChunk);
        return townClaimedChunk;
    }

    @Override
    public RegionClaimedChunk claimRegionChunk(Chunk chunk, String id) {
        RegionClaimedChunk regionClaimedChunk = new RegionClaimedChunk(chunk, id);
        databaseManager.save(regionClaimedChunk);
        return regionClaimedChunk;
    }

    @Override
    public NationClaimedChunk claimNationChunk(Chunk chunk, String id) {
        NationClaimedChunk nationClaimedChunk = new NationClaimedChunk(chunk, id);
        databaseManager.save(nationClaimedChunk);
        return nationClaimedChunk;
    }

    @Override
    public void save() {
        // No need to implement save as the database handles it in real-time
    }

    private ChunkDatabase load(String id) {
        ChunkData data = databaseManager.load(id);
        if(data == null){
            return null;
        }

        return switch (data){
            case LandmarkClaimedChunk landmarkChunk -> new LandmarkChunkDatabase(landmarkChunk, databaseManager);
            case TownClaimedChunk townChunk -> new TownChunkDatabase(townChunk, databaseManager);
            case RegionClaimedChunk regionChunk -> new RegionChunkDatabase(regionChunk, databaseManager);
            case NationClaimedChunk nationChunk -> new NationChunkDatabase(nationChunk, databaseManager);
            default -> throw new IllegalStateException("Unexpected chunk type: " + data.getType());
        };
    }
}
