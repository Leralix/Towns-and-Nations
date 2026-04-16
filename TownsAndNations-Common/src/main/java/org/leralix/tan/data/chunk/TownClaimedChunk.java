package org.leralix.tan.data.chunk;

import org.bukkit.Chunk;
import org.bukkit.entity.EntityType;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.data.territory.Town;

public class TownClaimedChunk extends TerritoryChunkData implements TownChunk {

    public TownClaimedChunk(Chunk chunk, String owner) {
        super(chunk, owner);
    }

    public TownClaimedChunk(int x, int z, String worldUUID, String ownerID) {
        super(x, z, worldUUID, ownerID);
    }

    @Override
    public String getName() {
        return getTown().getName();
    }

    @Override
    public Town getTown() {
        return TownsAndNations.getPlugin().getTownStorage().get(getOwnerID());
    }

    @Override
    public boolean canEntitySpawn(EntityType entityType) {
        return getTown().getChunkSettings().getSpawnControl(entityType.toString()).canSpawn();
    }

    @Override
    public boolean canTerritoryClaim(Territory territoryData) {
        return territoryData.canConquerChunk(this);
    }

    @Override
    public ChunkType getType() {
        return ChunkType.TOWN;
    }
}
