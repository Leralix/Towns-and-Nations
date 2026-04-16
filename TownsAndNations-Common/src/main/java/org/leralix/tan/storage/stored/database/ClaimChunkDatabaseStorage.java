package org.leralix.tan.storage.stored.database;

import org.bukkit.Chunk;
import org.leralix.tan.data.chunk.*;
import org.leralix.tan.storage.stored.ClaimStorage;
import org.leralix.tan.utils.constants.database.RedisConfig;
import org.leralix.tan.utils.territory.ChunkUtil;

import java.util.Collection;

public class ClaimChunkDatabaseStorage extends DatabaseStorage<ChunkDatabase, IClaimedChunk> implements ClaimStorage {

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
        return databaseManager.getAll();
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
        IClaimedChunk data = databaseManager.load(id);
        if(data == null){
            return null;
        }

        return switch (data){
            case LandmarkChunk landmarkChunk -> new LandmarkChunkDatabase(databaseManager, landmarkChunk);
            case TownChunk townChunk -> new TownChunkDatabase(databaseManager, townChunk);
            case RegionChunk regionChunk -> new RegionChunkDatabase(databaseManager, regionChunk);
            case NationChunk nationChunk -> new NationChunkDatabase(databaseManager, nationChunk);
            default -> throw new IllegalStateException("Unexpected chunk type: " + data.getType());
        };
    }
}
