package org.leralix.tan.dataclass.chunk;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.Landmark;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.permissions.ChunkPermissionType;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.LandmarkStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.territory.ChunkUtil;
import org.leralix.tan.utils.text.TanChatUtils;

public class LandmarkClaimedChunk extends ClaimedChunk2 {
  public LandmarkClaimedChunk(Chunk chunk, String owner) {
    super(chunk, owner);
  }

  public LandmarkClaimedChunk(int x, int z, String worldUUID, String ownerID) {
    super(x, z, worldUUID, ownerID);
  }

  public String getName() {
    return TownDataStorage.getInstance().getSync(getOwnerID()).getName();
  }

  @Override
  protected boolean canPlayerDoInternal(
      Player player, ChunkPermissionType permissionType, Location location) {

    if (permissionType == ChunkPermissionType.INTERACT_CHEST
        || permissionType == ChunkPermissionType.INTERACT_DOOR
        || permissionType == ChunkPermissionType.ATTACK_PASSIVE_MOB
        || permissionType == ChunkPermissionType.INTERACT_BUTTON
        || permissionType == ChunkPermissionType.INTERACT_REDSTONE
        || permissionType == ChunkPermissionType.INTERACT_FURNACE
        || permissionType == ChunkPermissionType.INTERACT_ITEM_FRAME
        || permissionType == ChunkPermissionType.INTERACT_ARMOR_STAND
        || permissionType == ChunkPermissionType.INTERACT_DECORATIVE_BLOCK
        || permissionType == ChunkPermissionType.INTERACT_MUSIC_BLOCK
        || permissionType == ChunkPermissionType.USE_LEAD
        || permissionType == ChunkPermissionType.USE_SHEARS) {
      return true;
    }

    TanChatUtils.message(player, Lang.CANNOT_DO_IN_LANDMARK.get(player));
    return false;
  }

  public Landmark getLandMark() {
    return LandmarkStorage.getInstance().getSync(ownerID);
  }

  public void unclaimChunk(Player player) {
    TanChatUtils.message(player, Lang.CANNOT_UNCLAIM_LANDMARK_CHUNK.get(player));
  }

  public void playerEnterClaimedArea(Player player, boolean displayTerritoryColor) {
    player.sendActionBar(Component.text(Lang.PLAYER_ENTER_LANDMARK_CHUNK.getDefault()));
  }

  @Override
  public boolean canEntitySpawn(EntityType entityType) {
    return true;
  }

  @Override
  public Component getMapIcon(LangType langType) {
    String hoverText =
        "x : "
            + super.getMiddleX()
            + " z : "
            + super.getMiddleZ()
            + "\n"
            + getLandMark().getName()
            + "\n"
            + Lang.LEFT_CLICK_TO_CLAIM.get(langType);
    return Component.text("⬛")
        .color(NamedTextColor.GOLD)
        .hoverEvent(HoverEvent.showText(Component.text(hoverText)));
  }

  @Override
  public boolean canTerritoryClaim(TerritoryData territoryData) {
    return false;
  }

  @Override
  public boolean canTerritoryClaim(Player player, TerritoryData territoryData) {
    TanChatUtils.message(player, Lang.CANNOT_CLAIM_LANDMARK.get(player));
    return false;
  }

  @Override
  public boolean isClaimed() {
    return true;
  }

  @Override
  public boolean canExplosionGrief() {
    return false;
  }

  @Override
  public boolean canFireGrief() {
    return false;
  }

  @Override
  public boolean canPVPHappen() {
    return true;
  }

  @Override
  public boolean canMobGrief() {
    return false;
  }

  @Override
  public ChunkType getType() {
    return ChunkType.LANDMARK;
  }

  @Override
  public void notifyUpdate() {
    if (Constants.isLandmarkClaimRequiresEncirclement() || !isClaimed()) {
      removeIfNotEncircled();
    }
  }

  private void removeIfNotEncircled() {
    Landmark landmark = getLandMark();

    if (ChunkUtil.isChunkEncirecledBy(
        this, chunk -> chunk.getOwnerID().equals(landmark.getOwnerID()))) {
      return;
    }
    getLandMark().removeOwnership();
  }
}
