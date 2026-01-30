package org.leralix.tan.data.chunk;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.leralix.tan.data.building.property.PropertyData;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.data.territory.TownData;
import org.leralix.tan.data.territory.permission.ChunkPermissionType;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.territory.ChunkUtil;
import org.leralix.tan.utils.text.TanChatUtils;

public class TownClaimedChunk extends TerritoryChunk {

    public TownClaimedChunk(Chunk chunk, String owner) {
        super(chunk, owner);
    }

    public TownClaimedChunk(int x, int z, String worldUUID, String ownerID) {
        super(x, z, worldUUID, ownerID);
    }

    public String getName() {
        return getTown().getName();
    }

    public TownData getTown() {
        return TownDataStorage.getInstance().get(getOwnerID());
    }

    @Override
    protected boolean canPlayerDoInternal(Player player, ITanPlayer tanPlayer, ChunkPermissionType permissionType, Location location) {

        //Location is in a property, and players own or rent it
        TownData ownerTown = getTown();
        PropertyData property = ownerTown.getProperty(location);
        if (property != null) {
            if (property.isPlayerAllowed(permissionType, tanPlayer)) {
                return true;
            } else {
                TanChatUtils.message(player, property.getDenyMessage(tanPlayer.getLang()));
                return false;
            }
        }
        return commonTerritoryCanPlayerDo(player, permissionType, tanPlayer);
    }

    @Override
    public boolean canEntitySpawn(EntityType entityType) {
        return getTown().getChunkSettings().getSpawnControl(entityType.toString()).canSpawn();
    }

    @Override
    public boolean canTerritoryClaim(TerritoryData territoryData) {
        return territoryData.canConquerChunk(this);
    }

    @Override
    public ChunkType getType() {
        return ChunkType.TOWN;
    }

    @Override
    public void notifyUpdate() {
        if (!Constants.allowNonAdjacentChunksFor(getOwner())) {
            ChunkUtil.unclaimIfNoLongerSupplied(this);
        }
    }
}
