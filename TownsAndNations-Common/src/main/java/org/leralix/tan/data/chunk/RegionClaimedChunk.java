package org.leralix.tan.data.chunk;

import org.bukkit.Chunk;
import org.bukkit.entity.EntityType;
import org.leralix.tan.data.territory.RegionData;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.storage.stored.RegionDataStorage;

public class RegionClaimedChunk extends TerritoryChunk {

    public RegionClaimedChunk(Chunk chunk, String owner) {
        super(chunk, owner);
    }

    public RegionClaimedChunk(int x, int z, String worldUUID, String ownerID) {
        super(x, z, worldUUID, ownerID);
    }

    public String getName() {
        return getOwner().getName();
    }

    public RegionData getRegion() {
        return RegionDataStorage.getInstance().get(getOwnerID());
    }

    @Override
    public boolean canEntitySpawn(EntityType entityType) {
        return true;
    }

    @Override
    public boolean canTerritoryClaim(TerritoryData territoryData) {
        if (territoryData.canConquerChunk(this))
            return true;

        // if the town is part of this specific region, they can claim
        return getRegion().getSubjects().contains(territoryData);
    }

    @Override
    public ChunkType getType() {
        return ChunkType.REGION;
    }
}
