package org.tan.TownsAndNations.DataClass.newChunkData;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.DataClass.RegionData;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.enums.ChunkPermissionType;
import org.tan.TownsAndNations.enums.TownRolePermission;
import org.tan.TownsAndNations.storage.DataStorage.NewClaimedChunkStorage;
import org.tan.TownsAndNations.storage.DataStorage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.DataStorage.RegionDataStorage;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;

import java.util.Objects;

import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;

public class RegionClaimedChunk extends ClaimedChunk2{


    public RegionClaimedChunk(Chunk chunk, String owner) {
        super(chunk, owner);
    }
    public RegionClaimedChunk(int x, int z, String worldUUID, String ownerID) {
        super(x,z,worldUUID,ownerID);
    }

    public String getName(){
        return RegionDataStorage.get(getID()).getName();
    }

    @Override
    public boolean canPlayerDo(Player player, ChunkPermissionType permissionType) {
        PlayerData playerData = PlayerDataStorage.get(player);
        RegionData region = RegionDataStorage.get(ownerID);
        return region.isPlayerInRegion(playerData);
    }

    public RegionData getRegion() {
        return RegionDataStorage.get(getID());
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

        if(!claimedChunk.getID().equals(regionData.getID())){
            RegionData otherRegion = RegionDataStorage.get(claimedChunk.getID());
            player.sendMessage(getTANString() + Lang.UNCLAIMED_CHUNK_NOT_RIGHT_REGION.get(otherRegion.getName()));
        }

        if(!regionData.isPlayerLeader(playerStat)){
            player.sendMessage(getTANString() + Lang.PLAYER_NOT_LEADER_OF_REGION.get());
            return;
        }

        player.sendMessage(getTANString() + Lang.UNCLAIMED_CHUNK_SUCCESS.get(townStat.getNumberOfClaimedChunk(),townStat.getTownLevel().getChunkCap()));
        NewClaimedChunkStorage.unclaimChunk(chunk);

    }

    public void playerEnterClaimedArea(Player player){
        RegionData region = getRegion();
        player.sendMessage( getTANString() + Lang.CHUNK_ENTER_REGION.get(region.getName()));
    }
}
