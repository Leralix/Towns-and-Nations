package org.leralix.tan.utils;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.config.ConfigTag;
import org.leralix.tan.utils.config.ConfigUtil;

import static org.leralix.tan.utils.ChatUtils.getTANString;

/**
 *
 */
public class ChunkUtil {

    public static void claimChunkForRegion(final @NotNull Player player,final @NotNull Chunk chunk) {

        //No town
        PlayerData playerStat = PlayerDataStorage.get(player.getUniqueId().toString());
        if(!playerStat.haveTown()){
            player.sendMessage(getTANString() + Lang.PLAYER_NO_TOWN.get());
            return;
        }

        //No region
        TownData townData = TownDataStorage.get(player);
        if(!townData.haveOverlord()){
            player.sendMessage(getTANString() + Lang.TOWN_NO_REGION.get());
            return;
        }

        RegionData regionData = townData.getOverlord();

        //Not leader of the region
        if(!regionData.doesPlayerHavePermission(playerStat, RolePermission.CLAIM_CHUNK)){
            player.sendMessage(getTANString() + Lang.PLAYER_NOT_LEADER_OF_REGION.get());
            return;
        }
        int cost = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("CostOfRegionChunk",5);

        if(regionData.getBalance() < cost){
            player.sendMessage(getTANString() + Lang.REGION_NOT_ENOUGH_MONEY_EXTENDED.get(cost - regionData.getBalance()));
            return;
        }

        regionData.removeFromBalance(cost);
        NewClaimedChunkStorage.claimRegionChunk(chunk, regionData.getID());
        player.sendMessage(getTANString() + Lang.CHUNK_CLAIMED_SUCCESS_REGION.get());

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
        if(!townData.doesPlayerHavePermission(playerStat , RolePermission.CLAIM_CHUNK)){
            player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.get());
            return;
        }

        //Chunk limit reached
        if(!townData.canClaimMoreChunk()){
            player.sendMessage(getTANString() + Lang.MAX_CHUNK_LIMIT_REACHED.get());
            return;
        }

        int cost = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("CostOfTownChunk",0);
        if(townData.getBalance() < cost){
            player.sendMessage(getTANString() + Lang.TOWN_NOT_ENOUGH_MONEY_EXTENDED.get(cost - townData.getBalance()));
            return;
        }

        ClaimedChunk2 claimedChunk = NewClaimedChunkStorage.get(chunkToClaim);
        boolean overClaimed = false;

        if(claimedChunk.isClaimed()){
            if(claimedChunk.canBeOverClaimed(townData))
                overClaimed = true;
            else{
                player.sendMessage(getTANString() + Lang.CHUNK_ALREADY_CLAIMED_WARNING.get(claimedChunk.getOwner().getName()));
                return;
            }
        }

        if(townData.getNumberOfClaimedChunk() != 0 &&
                !NewClaimedChunkStorage.isAdjacentChunkClaimedBySameTown(chunkToClaim,townData.getID()) &&
                !ConfigUtil.getCustomConfig(ConfigTag.MAIN).getBoolean("TownAllowNonAdjacentChunks",false))
        {
            player.sendMessage(getTANString() + Lang.CHUNK_NOT_ADJACENT.get());
            return;
        }

        TownClaim(overClaimed, chunkToClaim, townData, player);


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
