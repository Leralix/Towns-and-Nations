package org.leralix.tan.dataclass.chunk;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.Landmark;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.storage.stored.LandmarkStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.TanChatUtils;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.enums.permissions.ChunkPermissionType;

public class LandmarkClaimedChunk extends ClaimedChunk2{
    public LandmarkClaimedChunk(Chunk chunk, String owner) {
        super(chunk, owner);
    }

    public LandmarkClaimedChunk(int x, int z, String worldUUID, String ownerID) {
        super(x,z,worldUUID,ownerID);
    }
    public String getName(){
        return TownDataStorage.getInstance().get(getOwnerID()).getName();
    }

    @Override
    public boolean canPlayerDo(Player player, ChunkPermissionType permissionType, Location location) {

        if(permissionType == ChunkPermissionType.INTERACT_CHEST ||
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
                permissionType == ChunkPermissionType.USE_SHEARS){
            return true;
        }


        player.sendMessage(TanChatUtils.getTANString() + Lang.CANNOT_DO_IN_LANDMARK.get());
        return false;
    }


    public Landmark getLandMark(){
        return LandmarkStorage.getInstance().get(ownerID);
    }
    public void unclaimChunk(Player player){
        player.sendMessage(TanChatUtils.getTANString() + Lang.CANNOT_UNCLAIM_LANDMARK_CHUNK.get());
    }

    public void playerEnterClaimedArea(Player player){
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Lang.PLAYER_ENTER_LANDMARK_CHUNK.get()));
    }

    @Override
    public boolean canEntitySpawn(EntityType entityType) {
       return true;
    }

    @Override
    public TextComponent getMapIcon(PlayerData playerData) {
        TextComponent textComponent = new TextComponent("⬛");
        textComponent.setColor(ChatColor.GOLD);
        textComponent.setHoverEvent(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                new Text("x : " + super.getX() + " z : " + super.getZ() + "\n" +
                        ChatColor.GOLD + getLandMark().getName() + "\n" +
                        Lang.LEFT_CLICK_TO_CLAIM.get())));
        return textComponent;
    }

    @Override
    public boolean canTerritoryClaim(Player player, TerritoryData townData) {
        player.sendMessage(TanChatUtils.getTANString() + Lang.CANNOT_CLAIM_LANDMARK.get());
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
}
