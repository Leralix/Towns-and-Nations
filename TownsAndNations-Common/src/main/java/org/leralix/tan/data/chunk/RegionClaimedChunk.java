package org.leralix.tan.data.chunk;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.RegionData;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.data.territory.permission.ChunkPermissionType;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.territory.ChunkUtil;

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

    @Override
    protected boolean canPlayerDoInternal(Player player, ITanPlayer tanPlayer, ChunkPermissionType permissionType, Location location) {
        return commonTerritoryCanPlayerDo(player, permissionType, tanPlayer);
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

    @Override
    public void notifyUpdate() {
        if (!Constants.allowNonAdjacentChunksFor(getOwner())) {
            ChunkUtil.unclaimIfNoLongerSupplied(this);
        }
    }

}
