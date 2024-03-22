package org.tan.TownsAndNations.utils;

import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.DataClass.TownRank;
import org.tan.TownsAndNations.DataClass.legacy.ClaimedChunk;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.storage.DataStorage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.Legacy.ClaimedChunkStorage;
import org.tan.TownsAndNations.storage.DataStorage.NewClaimedChunkStorage;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class UpdateUtil {

    public static void update(){
        updateNewChunkData();
        updateRankID();
    }

    private static void updateRankID() {

        if(TownDataStorage.getTownMap().isEmpty())
            return;


        /*
        if(TownDataStorage.getTownMap().values().iterator().next().getRanks().get(0) != null){
            System.out.println("Rank ID's are already updated");
            return;
        }
        */


        for(TownData townData : TownDataStorage.getTownMap().values()){
            int i = 0;
            for (TownRank townRank : townData.getOldRanks()) {
                townRank.setID(i);
                i++;
            }
            HashMap<Integer,TownRank> newMap = new HashMap<>();
            for(TownRank townRank : townData.getOldRanks()){
                newMap.put(townRank.getID(), townRank);
            }
            townData.setNewRanks(newMap);

        }

        for(PlayerData playerData : PlayerDataStorage.getLists()){
            TownData playerTownData = playerData.getTown();

            TownRank townRank = playerTownData.getOldRank(playerData.getOldRank());
            playerData.setTownRankID(townRank.getID());
        }

    }

    public static void updateDatabase() {
        //TownDataStorage.UpdateTownDataWithColor();
    }

    public static void updateNewChunkData() {
        if (ClaimedChunkStorage.getClaimedChunksMap().isEmpty()) {
            return;
        }
        Iterator<Map.Entry<String, ClaimedChunk>> iterator = ClaimedChunkStorage.getClaimedChunksMap().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ClaimedChunk> entry = iterator.next();
            ClaimedChunk oldClaimedChunk = entry.getValue();
            NewClaimedChunkStorage.claimTownChunk(oldClaimedChunk.getChunk(), oldClaimedChunk.getID());
            iterator.remove();
        }
        TownsAndNations.getPlugin().getLogger().info("-Claimed chunks have been updated to the new system");
    }


    public static void UpdateTownToNewUpgradeSystem() {

        for(TownData town : TownDataStorage.getTownMap().values()) {
            int townLevel = town.getTownLevel().getTownLevel();
            int townChunkCapLevel = town.getTownLevel().getChunkCapLevel();
            int townPlayerCap = town.getTownLevel().getPlayerCapLevel();

            town.addToBalance(townLevel * 1200 + townChunkCapLevel * 800 + townPlayerCap * 800);
        }
    }


}
