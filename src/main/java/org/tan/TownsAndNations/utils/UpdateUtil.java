package org.tan.TownsAndNations.utils;

import org.tan.TownsAndNations.DataClass.ClaimedChunk;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.DataClass.TownRank;
import org.tan.TownsAndNations.enums.TownRankEnum;
import org.tan.TownsAndNations.storage.ClaimedChunkStorage;
import org.tan.TownsAndNations.storage.NewClaimedChunkStorage;
import org.tan.TownsAndNations.storage.TownDataStorage;

public class UpdateUtil {

    public static void update(){
        updateNewChunkData();
    }

    public static void updateDatabase() {
        //TownDataStorage.UpdateTownDataWithColor();
    }

    public static void updateNewChunkData() {
        if (ClaimedChunkStorage.getClaimedChunksMap().isEmpty()) {
            System.out.println("No old chunk to convert to new chunk data");
            return;
        }
        for (ClaimedChunk oldClaimedChunk : ClaimedChunkStorage.getClaimedChunksMap().values()) {
            NewClaimedChunkStorage.claimTownChunk(oldClaimedChunk.getChunk(), oldClaimedChunk.getID());
        }
        System.out.println("All old chunk has been converted");
    }

    public static void UpdateTownToNewUpgradeSystem() {

        for(TownData town : TownDataStorage.getTownList().values()) {
            int townLevel = town.getTownLevel().getTownLevel();
            int townChunkCapLevel = town.getTownLevel().getChunkCapLevel();
            int townPlayerCap = town.getTownLevel().getPlayerCapLevel();

            town.addToBalance(townLevel * 1200 + townChunkCapLevel * 800 + townPlayerCap * 800);
        }
    }


}
