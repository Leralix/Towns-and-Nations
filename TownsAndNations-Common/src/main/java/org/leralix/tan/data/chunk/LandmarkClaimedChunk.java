package org.leralix.tan.data.chunk;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.leralix.tan.data.building.landmark.Landmark;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.data.territory.permission.ChunkPermissionType;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.LandmarkStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.text.TanChatUtils;

public class LandmarkClaimedChunk extends ClaimedChunk {

    /**
     * The ID of the landmark on this chunk
     * The name "ownerID" is used for compatibility with old ClaimedChunk2 structure.
     */
    private final String ownerID;

    public LandmarkClaimedChunk(Chunk chunk, String owner) {
        super(chunk);
        this.ownerID = owner;
    }

    public LandmarkClaimedChunk(int x, int z, String worldUUID, String ownerID) {
        super(x, z, worldUUID);
        this.ownerID = ownerID;
    }

    public String getName() {
        return TownDataStorage.getInstance().get(ownerID).getName();
    }

    @Override
    protected boolean canPlayerDoInternal(Player player, ITanPlayer tanPlayer, ChunkPermissionType permissionType, Location location) {

        if (permissionType == ChunkPermissionType.INTERACT_CHEST ||
                permissionType == ChunkPermissionType.INTERACT_DOOR ||
                permissionType == ChunkPermissionType.ATTACK_PASSIVE_MOB ||
                permissionType == ChunkPermissionType.INTERACT_BUTTON ||
                permissionType == ChunkPermissionType.INTERACT_REDSTONE ||
                permissionType == ChunkPermissionType.INTERACT_FURNACE ||
                permissionType == ChunkPermissionType.INTERACT_ITEM_FRAME ||
                permissionType == ChunkPermissionType.INTERACT_ARMOR_STAND ||
                permissionType == ChunkPermissionType.INTERACT_DECORATIVE_BLOCK ||
                permissionType == ChunkPermissionType.INTERACT_MUSIC_BLOCK ||
                permissionType == ChunkPermissionType.USE_LEAD ||
                permissionType == ChunkPermissionType.USE_SHEARS) {
            return true;
        }


        TanChatUtils.message(player, Lang.CANNOT_DO_IN_LANDMARK.get(player));
        return false;
    }

    @Override
    protected void playerCantPerformAction(Player player) {
        TanChatUtils.message(player, Lang.CANNOT_DO_IN_LANDMARK.get(player));
    }


    public Landmark getLandMark() {
        return LandmarkStorage.getInstance().get(ownerID);
    }

    public void unclaimChunk(Player player) {
        TanChatUtils.message(player, Lang.CANNOT_UNCLAIM_LANDMARK_CHUNK.get(player));
    }

    public void playerEnterClaimedArea(Player player, boolean displayTerritoryColor) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Lang.PLAYER_ENTER_LANDMARK_CHUNK.getDefault()));
    }

    @Override
    public boolean canEntitySpawn(EntityType entityType) {
        return true;
    }

    @Override
    public TextComponent getMapIcon(LangType langType) {
        TextComponent textComponent = new TextComponent("⬛");
        textComponent.setColor(ChatColor.GOLD);
        textComponent.setHoverEvent(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                new Text("x : " + super.getMiddleX() + " z : " + super.getMiddleZ() + "\n" +
                        ChatColor.GOLD + getLandMark().getName() + "\n" +
                        Lang.LEFT_CLICK_TO_CLAIM.get(langType))));
        return textComponent;
    }

    @Override
    public boolean canTerritoryClaim(Player player, TerritoryData territoryData) {
        TanChatUtils.message(player, Lang.CANNOT_CLAIM_LANDMARK.get(player));
        return false;
    }

    @Override
    public boolean canTerritoryClaim(TerritoryData territoryData) {
        return false;
    }

    @Override
    public boolean isClaimed() {
        Landmark landmark = getLandMark();
        return landmark != null && landmark.isOwned();
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
        if (Constants.isLandmarkClaimRequiresEncirclement() && isClaimed()) {
            Landmark landmark = getLandMark();

            if(!landmark.isEncircledBy(landmark.getOwner())){
                landmark.removeOwnership();
            }
        }
    }
}
