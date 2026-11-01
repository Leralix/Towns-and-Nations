package org.leralix.tan.data.chunk;

import org.bukkit.Chunk;
import org.leralix.tan.TownsAndNations;
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
    public ChunkType getType() {
        return ChunkType.TOWN;
    }
}
