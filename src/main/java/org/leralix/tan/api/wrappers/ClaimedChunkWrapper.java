package org.leralix.tan.api.wrappers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.permissions.ChunkPermissionType;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.TerritoryUtil;
import org.tan.api.enums.EChunkPermission;
import org.tan.api.interfaces.*;

import java.util.Optional;
import java.util.UUID;

public class ClaimedChunkWrapper implements TanClaimedChunk {


    private final ClaimedChunk2 claimedChunk;

    private ClaimedChunkWrapper(ClaimedChunk2 claimedChunk) {
        this.claimedChunk = claimedChunk;
    }

    public static TanClaimedChunk of(ClaimedChunk2 claimedChunk) {
        if (claimedChunk == null) {
            return null;
        }
        return new ClaimedChunkWrapper(claimedChunk);
    }

    @Override
    public int getX() {
        return claimedChunk.getX();
    }

    @Override
    public int getZ() {
        return claimedChunk.getZ();
    }

    @Override
    public UUID getWorldUUID() {
        return UUID.fromString(claimedChunk.getWorldUUID());
    }

    @Override
    public String getworldName() {
        return claimedChunk.getWorld().getName();
    }

    @Override
    public Boolean isClaimed() {
        return claimedChunk.isClaimed();
    }

    @Override
    public Optional<UUID> getOwnerID() {
        if(claimedChunk.getOwnerID() == null){
            return Optional.empty();
        }
        return Optional.of(UUID.fromString(claimedChunk.getOwnerID()));
    }

    @Override
    public void unclaim() {
        NewClaimedChunkStorage.getInstance().unclaimChunk(claimedChunk);
    }

    @Override
    public boolean canClaim(TanTerritory tanTerritory) {
        TerritoryData territoryData = TerritoryUtil.getTerritory(tanTerritory.getID());
        if(territoryData != null){
            return claimedChunk.canTerritoryClaim(null, territoryData);
        }
        return false;
    }

    @Override
    public void claim(TanTerritory tanTerritory) {
        if(tanTerritory == null){
            return;
        }
        if(tanTerritory instanceof TanTown){
            NewClaimedChunkStorage.getInstance().claimTownChunk(claimedChunk.getChunk(), tanTerritory.getID());
        }
        if(tanTerritory instanceof TanRegion){
            NewClaimedChunkStorage.getInstance().claimRegionChunk(claimedChunk.getChunk(), tanTerritory.getID());
        }
    }

    @Override
    public boolean canBeGriefByExplosion() {
        return claimedChunk.canExplosionGrief();
    }

    @Override
    public boolean canBeGriefByFire() {
        return claimedChunk.canFireGrief();
    }

    @Override
    public boolean canPvpHappen() {
        return claimedChunk.canPVPHappen();
    }

    @Override
    public boolean canPlayerDoAction(TanPlayer tanPlayer, EChunkPermission permission, Location location) {
        Player player = Bukkit.getPlayer(tanPlayer.getUUID());
        if(player == null) {
            return false; // Player is not online
        }
        ChunkPermissionType chunkPermissionType = ChunkPermissionType.valueOf(permission.name());
        return claimedChunk.canPlayerDo(player, chunkPermissionType, location);
    }
}
