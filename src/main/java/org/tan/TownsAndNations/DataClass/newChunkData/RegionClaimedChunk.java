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
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.DataClass.territoryData.ITerritoryData;
import org.tan.TownsAndNations.DataClass.territoryData.RegionData;
import org.tan.TownsAndNations.DataClass.territoryData.TownData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.enums.ChunkPermissionType;
import org.tan.TownsAndNations.storage.DataStorage.NewClaimedChunkStorage;
import org.tan.TownsAndNations.storage.DataStorage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.DataStorage.RegionDataStorage;

import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;

public class RegionClaimedChunk extends ClaimedChunk2{


    public RegionClaimedChunk(Chunk chunk, String owner) {
        super(chunk, owner);
    }
    public RegionClaimedChunk(int x, int z, String worldUUID, String ownerID) {
        super(x,z,worldUUID,ownerID);
    }

    public String getName(){
        return RegionDataStorage.get(getOwnerID()).getName();
    }
    @Override
    public boolean canPlayerDo(Player player, ChunkPermissionType permissionType, Location location) {
        PlayerData playerData = PlayerDataStorage.get(player);
        RegionData region = RegionDataStorage.get(ownerID);
        return region.isPlayerInRegion(playerData);
    }

    public RegionData getRegion() {
        return RegionDataStorage.get(getOwnerID());
    }

    public void unclaimChunk(Player player, Chunk chunk){
        PlayerData playerStat = PlayerDataStorage.get(player.getUniqueId().toString());
        if(!playerStat.haveTown()){
            player.sendMessage(getTANString() + Lang.PLAYER_NO_TOWN.get());
            return;
        }

        if(!playerStat.haveRegion()){
            player.sendMessage(getTANString() + Lang.TOWN_NO_REGION.get());
            return;
        }

        TownData townStat = playerStat.getTown();
        RegionData regionData = playerStat.getRegion();

        ClaimedChunk2 claimedChunk = NewClaimedChunkStorage.get(chunk);

        if(!claimedChunk.getOwnerID().equals(regionData.getID())){
            RegionData otherRegion = RegionDataStorage.get(claimedChunk.getOwnerID());
            player.sendMessage(getTANString() + Lang.UNCLAIMED_CHUNK_NOT_RIGHT_REGION.get(otherRegion.getName()));
        }

        if(!playerStat.isRegionLeader()){
            player.sendMessage(getTANString() + Lang.PLAYER_NOT_LEADER_OF_REGION.get());
            return;
        }

        NewClaimedChunkStorage.unclaimChunk(chunk);
        player.sendMessage(getTANString() + Lang.UNCLAIMED_CHUNK_SUCCESS_REGION.get(regionData.getNumberOfClaimedChunk(),townStat.getTownLevel().getChunkCap()));

    }

    public void playerEnterClaimedArea(Player player){
        RegionData region = getRegion();
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Lang.PLAYER_ENTER_REGION_CHUNK.get(region.getName())));
    }

    @Override
    public boolean canEntitySpawn(EntityType entityType) {
        return true;
    }

    @Override
    public TextComponent getMapIcon(PlayerData playerData) {
        TextComponent textComponent = new TextComponent(getRegion().getChunkColorClass() + "⬛");
        textComponent.setHoverEvent(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                new Text("x : " + super.getX() + " z : " + super.getZ() + "\n" +
                        getRegion().getColoredName() + "\n" +
                        Lang.LEFT_CLICK_TO_CLAIM.get())));        return textComponent;
    }

    @Override
    public boolean canPlayerClaim(Player player, ITerritoryData territoryData) {

        if(territoryData.canConquerChunk(this))
            return true;

        if(territoryData.haveOverlord()){
            if(territoryData.getOverlord().getID().equals(getOwnerID())){
                return true; // if the town is part of this specific region they can claim
            }
        }
        player.sendMessage(getTANString() + Lang.CHUNK_ALREADY_CLAIMED_WARNING.get(getOwner().getColoredName()));
        return false;
    }

}
