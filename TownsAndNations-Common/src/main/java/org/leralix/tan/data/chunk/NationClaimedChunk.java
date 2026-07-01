package org.leralix.tan.data.chunk;

import org.bukkit.Chunk;
import org.bukkit.entity.EntityType;

public class NationClaimedChunk extends TerritoryChunkData implements NationChunk {

    public NationClaimedChunk(Chunk chunk, String owner) {
        super(chunk, owner);
    }

    public NationClaimedChunk(int x, int z, String worldUUID, String ownerID) {
        super(x, z, worldUUID, ownerID);
    }

    @Override
    public String getName() {
        return getOwner().getName();
    }

    @Override
    public boolean canEntitySpawn(EntityType entityType) {
        return true;
    }

    @Override
    public ChunkType getType() {
        return ChunkType.NATION;
    }
}
