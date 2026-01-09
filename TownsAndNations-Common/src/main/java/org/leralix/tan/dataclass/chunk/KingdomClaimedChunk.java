package org.leralix.tan.dataclass.chunk;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.permissions.ChunkPermissionType;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.territory.ChunkUtil;

public class KingdomClaimedChunk extends TerritoryChunk {

    public KingdomClaimedChunk(Chunk chunk, String owner) {
        super(chunk, owner);
    }

    public KingdomClaimedChunk(int x, int z, String worldUUID, String ownerID) {
        super(x, z, worldUUID, ownerID);
    }

    @Override
    public String getName() {
        return getOwner().getName();
    }

    @Override
    protected boolean canPlayerDoInternal(Player player, ChunkPermissionType permissionType, Location location) {
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);
        return commonTerritoryCanPlayerDo(player, permissionType, tanPlayer);
    }

    @Override
    public void playerEnterClaimedArea(Player player, boolean displayTerritoryColor) {
        TerritoryData kingdomData = getOwner();
        if (kingdomData == null) {
            return;
        }

        TerritoryEnterMessageUtil.sendEnterTerritoryMessage(player, kingdomData, displayTerritoryColor);
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
        return getOwner().getVassalsID().contains(territoryData.getID());
    }

    @Override
    public boolean isClaimed() {
        return true;
    }

    @Override
    public ChunkType getType() {
        return ChunkType.KINGDOM;
    }

    @Override
    public void notifyUpdate() {
        if (!Constants.allowNonAdjacentChunksFor(getOwner())) {
            ChunkUtil.unclaimIfNoLongerSupplied(this);
        }
    }
}
