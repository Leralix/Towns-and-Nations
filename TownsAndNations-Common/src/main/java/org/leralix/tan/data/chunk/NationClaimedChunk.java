package org.leralix.tan.data.chunk;

import org.bukkit.Chunk;
import org.bukkit.entity.EntityType;
import org.leralix.tan.data.territory.TerritoryData;

public class NationClaimedChunk extends TerritoryChunk {

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
    public boolean canTerritoryClaim(TerritoryData territoryData) {
        if (territoryData.canConquerChunk(this)) {
            return true;
        }
        return getOwnerInternal().getVassalsID().contains(territoryData.getID());
    }

    @Override
    public ChunkType getType() {
        return ChunkType.NATION;
    }
}
