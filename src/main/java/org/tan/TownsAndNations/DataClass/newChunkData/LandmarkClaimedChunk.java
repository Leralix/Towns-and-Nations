package org.tan.TownsAndNations.DataClass.newChunkData;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.Landmark;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.DataClass.territoryData.ITerritoryData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.enums.ChunkPermissionType;
import org.tan.TownsAndNations.storage.DataStorage.LandmarkStorage;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;

import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;

public class LandmarkClaimedChunk extends ClaimedChunk2{
    public LandmarkClaimedChunk(Chunk chunk, String owner) {
        super(chunk, owner);
    }

    public LandmarkClaimedChunk(int x, int z, String worldUUID, String ownerID) {
        super(x,z,worldUUID,ownerID);
    }
    public String getName(){
        return TownDataStorage.get(getOwnerID()).getName();
    }

    @Override
    public boolean canPlayerDo(Player player, ChunkPermissionType permissionType, Location location) {

        if(permissionType == ChunkPermissionType.CHEST ||
                permissionType == ChunkPermissionType.OPEN_DOOR ||
                permissionType == ChunkPermissionType.ATTACK_PASSIVE_MOB ||
                permissionType == ChunkPermissionType.USE_BUTTONS ||
                permissionType == ChunkPermissionType.USE_REDSTONE ||
                permissionType == ChunkPermissionType.USE_FURNACE ||
                permissionType == ChunkPermissionType.INTERACT_ITEM_FRAME ||
                permissionType == ChunkPermissionType.INTERACT_ARMOR_STAND ||
                permissionType == ChunkPermissionType.DECORATIVE_BLOCK ||
                permissionType == ChunkPermissionType.MUSIC_BLOCK ||
                permissionType == ChunkPermissionType.LEAD ||
                permissionType == ChunkPermissionType.SHEARS){
            return true;
        }


        player.sendMessage(getTANString() + Lang.CANNOT_DO_IN_LANDMARK.get());
        return false;
    }


    public Landmark getLandMark(){
        return LandmarkStorage.get(ownerID);
    }
    public void unclaimChunk(Player player, Chunk chunk){
        player.sendMessage(getTANString() + Lang.CANNOT_UNCLAIM_LANDMARK_CHUNK.get());
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
        TextComponent textComponent = new TextComponent(ChatColor.GOLD + "⬛");
        textComponent.setBold(true);
        textComponent.setHoverEvent(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                new Text("x : " + super.getX() + " z : " + super.getZ() + "\n" +
                        ChatColor.GOLD + getLandMark().getName() + "\n" +
                        Lang.LEFT_CLICK_TO_CLAIM.get())));
        return textComponent;
    }

    @Override
    public boolean canPlayerClaim(Player player, ITerritoryData townData) {
        player.sendMessage(getTANString() + Lang.CANNOT_CLAIM_LANDMARK.get());
        return false;
    }
}
