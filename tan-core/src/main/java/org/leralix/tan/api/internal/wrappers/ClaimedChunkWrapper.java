package org.leralix.tan.api.internal.wrappers;

import java.util.Optional;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.chunk.ChunkType;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.permissions.ChunkPermissionType;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.tan.api.enums.EChunkPermission;
import org.tan.api.interfaces.*;

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

  public int getX() {
    return claimedChunk.getX();
  }

  public int getZ() {
    return claimedChunk.getZ();
  }

  public UUID getWorldUUID() {
    return UUID.fromString(claimedChunk.getWorldUUID());
  }

  public String getworldName() {
    return claimedChunk.getWorld().getName();
  }

  public Boolean isClaimed() {
    return claimedChunk.isClaimed();
  }

  public Optional<UUID> getOwnerID() {
    if (claimedChunk.getOwnerID() == null) {
      return Optional.empty();
    }
    return Optional.of(UUID.fromString(claimedChunk.getOwnerID()));
  }

  public void unclaim() {
    NewClaimedChunkStorage.getInstance().unclaimChunkAndUpdate(claimedChunk);
  }

  public boolean canClaim(TanTerritory tanTerritory) {
    TerritoryData territoryData = TerritoryUtil.getTerritory(tanTerritory.getID());
    if (territoryData != null) {
      return claimedChunk.canTerritoryClaim(null, territoryData);
    }
    return false;
  }

  public void claim(TanTerritory tanTerritory) {
    if (tanTerritory == null) {
      return;
    }
    if (tanTerritory instanceof TanTown) {
      NewClaimedChunkStorage.getInstance()
          .claimTownChunk(claimedChunk.getChunk(), tanTerritory.getID());
    }
    if (tanTerritory instanceof TanRegion) {
      NewClaimedChunkStorage.getInstance()
          .claimRegionChunk(claimedChunk.getChunk(), tanTerritory.getID());
    }
  }

  public boolean canBeGriefByExplosion() {
    return claimedChunk.canExplosionGrief();
  }

  public boolean canBeGriefByFire() {
    return claimedChunk.canFireGrief();
  }

  public boolean canPvpHappen() {
    return claimedChunk.canPVPHappen();
  }

  public boolean canPlayerDoAction(
      TanPlayer tanPlayer, EChunkPermission permission, Location location) {
    Player player = Bukkit.getPlayer(tanPlayer.getUUID());
    if (player == null) {
      return false; // Player is not online
    }
    ChunkPermissionType chunkPermissionType = ChunkPermissionType.valueOf(permission.name());
    return claimedChunk.canPlayerDo(player, chunkPermissionType, location);
  }

  @Override
  public org.bukkit.Chunk getChunk() {
    return claimedChunk.getChunk();
  }

  @Override
  public TanTown getOwner() {
    return null;
  }

  @Override
  public boolean isLandmark() {
    return claimedChunk.getType() == ChunkType.LANDMARK;
  }

  @Override
  public TanTerritory getTerritoryOwner() {
    return null;
  }
}
