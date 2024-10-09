package org.leralix.tan.dataclass.chunk;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.territory.ITerritoryData;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.Lang.Lang;
import org.leralix.tan.enums.ChunkPermissionType;
import org.leralix.tan.storage.DataStorage.NewClaimedChunkStorage;
import org.leralix.tan.storage.DataStorage.PlayerDataStorage;
import org.leralix.tan.storage.DataStorage.RegionDataStorage;

import static org.leralix.tan.utils.ChatUtils.getTANString;

public class RegionClaimedChunk extends ClaimedChunk2{


    public RegionClaimedChunk(Chunk chunk, String owner) {
        super(chunk, owner);
    }
    public RegionClaimedChunk(int x, int z, String worldUUID, String ownerID) {
        super(x,z,worldUUID,ownerID);
    }

    public String getName(){
        return getOwner().getName();
    }
    @Override
    public boolean canPlayerDo(Player player, ChunkPermissionType permissionType, Location location) {
        PlayerData playerData = PlayerDataStorage.get(player);

        if(!playerData.haveTown()){
            player.sendMessage(getTANString() + Lang.PLAYER_NO_TOWN.get());
        }

        if(!playerData.haveRegion()){
            player.sendMessage(getTANString() + Lang.TOWN_NO_REGION.get());
        }

        RegionData region = getRegion();

        if(!region.isPlayerInRegion(playerData)){
            playerCantPerformAction(player);
            return false;
        }

        return true;
    }

    public RegionData getRegion() {
        return RegionDataStorage.get(getOwnerID());
    }

    public void unclaimChunk(Player player){
        PlayerData playerStat = PlayerDataStorage.get(player.getUniqueId().toString());
        if(!playerStat.haveTown()){
            player.sendMessage(getTANString() + Lang.PLAYER_NO_TOWN.get());
            return;
        }

        if(!playerStat.haveRegion()){
            player.sendMessage(getTANString() + Lang.TOWN_NO_REGION.get());
            return;
        }

        RegionData regionData = playerStat.getRegion();

        if(!regionData.equals(getRegion())){
            player.sendMessage(getTANString() + Lang.UNCLAIMED_CHUNK_NOT_RIGHT_REGION.get(getRegion().getName()));
            return;
        }

        if(!playerStat.isRegionLeader()){
            player.sendMessage(getTANString() + Lang.PLAYER_NOT_LEADER_OF_REGION.get());
            return;
        }
        NewClaimedChunkStorage.unclaimChunk(this);
        player.sendMessage(getTANString() + Lang.UNCLAIMED_CHUNK_SUCCESS_REGION.get(regionData.getNumberOfClaimedChunk()));
    }

    public void playerEnterClaimedArea(Player player){
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Lang.PLAYER_ENTER_REGION_CHUNK.get(getRegion().getName())));
    }

    @Override
    public boolean canEntitySpawn(EntityType entityType) {
        return true;
    }

    @Override
    public TextComponent getMapIcon(PlayerData playerData) {
        TextComponent textComponent = new TextComponent("⬛");
        textComponent.setColor(getRegion().getChunkColor());
        textComponent.setHoverEvent(new HoverEvent(
            HoverEvent.Action.SHOW_TEXT,
            new Text("x : " + super.getX() + " z : " + super.getZ() + "\n" +
                    getRegion().getColoredName() + "\n" +
                    Lang.LEFT_CLICK_TO_CLAIM.get())));
        return textComponent;
    }

    @Override
    public boolean canPlayerClaim(Player player, ITerritoryData territoryData) {

        if(territoryData.canConquerChunk(this))
            return true;

        if(territoryData.haveOverlord() && territoryData.getOverlord().getID().equals(getOwnerID())){
            return true; // if the town is part of this specific region they can claim
        }

        player.sendMessage(getTANString() + Lang.CHUNK_ALREADY_CLAIMED_WARNING.get(getOwner().getColoredName()));
        return false;
    }

    @Override
    public boolean isClaimed() {
        return true;
    }

    @Override
    public boolean canBeOverClaimed(ITerritoryData territoryData) {
        return getRegion().getSubjects().contains(territoryData);
    }

}
