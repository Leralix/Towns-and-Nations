package org.leralix.tan.data.chunk;

import org.bukkit.Chunk;
import org.bukkit.entity.EntityType;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.territory.Region;

public class RegionClaimedChunk extends TerritoryChunkData implements RegionChunk {

    public RegionClaimedChunk(Chunk chunk, String owner) {
        super(chunk, owner);
    }

    public RegionClaimedChunk(int x, int z, String worldUUID, String ownerID) {
        super(x, z, worldUUID, ownerID);
    }

    @Override
    public String getName() {
        return getOwner().getName();
    }

    @Override
    public Region getRegion() {
        return TownsAndNations.getPlugin().getRegionStorage().get(getOwnerID());
    }

    @Override
    public boolean canEntitySpawn(EntityType entityType) {
        return true;
    }

    @Override
    public ChunkType getType() {
        return ChunkType.REGION;
    }
}
