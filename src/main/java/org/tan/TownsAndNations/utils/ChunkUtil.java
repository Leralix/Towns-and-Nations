package org.tan.TownsAndNations.utils;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.DataClass.RegionData;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.enums.TownRolePermission;
import org.tan.TownsAndNations.storage.DataStorage.NewClaimedChunkStorage;
import org.tan.TownsAndNations.storage.DataStorage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;

import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;

public class ChunkUtil {


    public static void claimChunkForRegion(Player player) {
        Chunk chunkToClaim = player.getLocation().getChunk();
        claimChunkForTown(player, chunkToClaim);
    }
    public static void claimChunkForRegion(Player player, Chunk chunk) {

        //No town
        PlayerData playerStat = PlayerDataStorage.get(player.getUniqueId().toString());
        if(!playerStat.haveTown()){
            player.sendMessage(getTANString() + Lang.PLAYER_NO_TOWN.get());
            return;
        }

        //No region
        TownData townData = TownDataStorage.get(player);
        if(!townData.haveRegion()){
            player.sendMessage(getTANString() + Lang.TOWN_NO_REGION.get());
            return;
        }

        RegionData regionData = townData.getRegion();

        //Not leader of the region
        if(!regionData.isPlayerLeader(playerStat)){
            player.sendMessage(getTANString() + Lang.PLAYER_NOT_LEADER_OF_REGION.get());
            return;
        }
        int cost = ConfigUtil.getCustomConfig("config.yml").getInt("CostOfRegionChunk",5);

        if(regionData.getBalance() < cost){
            player.sendMessage(getTANString() + Lang.REGION_NOT_ENOUGH_MONEY_EXTENDED.get(cost - regionData.getBalance()));
            return;
        }

        regionData.removeBalance(cost);
        NewClaimedChunkStorage.claimRegionChunk(chunk, regionData.getID());
        player.sendMessage(getTANString() + Lang.CHUNK_CLAIMED_SUCCESS_REGION.get());

    }

    public static void claimChunkForTown(Player player) {

        Chunk chunkToClaim = player.getLocation().getChunk();
        claimChunkForTown(player, chunkToClaim);
    }
    public static void claimChunkForTown(Player player, Chunk chunkToClaim) {

        //No town
        PlayerData playerStat = PlayerDataStorage.get(player.getUniqueId().toString());
        if(!playerStat.haveTown()){
            player.sendMessage(getTANString() + Lang.PLAYER_NO_TOWN.get());
            return;
        }

        //No permission
        TownData townData = TownDataStorage.get(player);
        if(!playerStat.hasPermission(TownRolePermission.CLAIM_CHUNK)){
            player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.get());
        }

        //Chunk limit reached
        if(!townData.canClaimMoreChunk()){
            player.sendMessage(getTANString() + Lang.MAX_CHUNK_LIMIT_REACHED.get());
            return;
        }
        boolean isRegionClaimed = false;

        int cost = ConfigUtil.getCustomConfig("config.yml").getInt("CostOfChunk",0);
        if(townData.getBalance() < cost){
            player.sendMessage(getTANString() + Lang.TOWN_NOT_ENOUGH_MONEY_EXTENDED.get(cost - townData.getBalance()));
            return;
        }


        //Chunk already claimed by the town
        if(NewClaimedChunkStorage.isChunkClaimed(chunkToClaim)){
            //If chunk belongs to the region in which the town is, then the town can get the chunk
            if(NewClaimedChunkStorage.isChunkClaimedByTownRegion(townData,chunkToClaim)){
                isRegionClaimed = true;
            }
            else{
                player.sendMessage(getTANString() + Lang.CHUNK_ALREADY_CLAIMED_WARNING.get(NewClaimedChunkStorage.getChunkOwnerName(chunkToClaim)));
                return;
            }
        }

        if(townData.getNumberOfClaimedChunk() != 0 &&
                !NewClaimedChunkStorage.isAdjacentChunkClaimedBySameTown(chunkToClaim,townData.getID()) &&
                ConfigUtil.getCustomConfig("config.yml").getBoolean("AllowNonAdjacentChunks",false))
        {
            player.sendMessage(getTANString() + Lang.CHUNK_NOT_ADJACENT.get());
            return;
        }

        TownClaim(isRegionClaimed, chunkToClaim, townData, player);


    }

    public static void TownClaim(boolean isRegionClaimed, Chunk chunkToClaim, TownData townData, Player player){
        if(isRegionClaimed)
            NewClaimedChunkStorage.unclaimChunk(chunkToClaim); //Unclaim the chunk so it can be claimed by the town afterward
        NewClaimedChunkStorage.claimTownChunk(chunkToClaim,townData.getID());

        player.sendMessage(getTANString() + Lang.CHUNK_CLAIMED_SUCCESS.get(
                townData.getNumberOfClaimedChunk(),
                townData.getTownLevel().getChunkCap())
        );
    }



}
