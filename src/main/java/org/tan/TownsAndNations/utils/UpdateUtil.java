package org.tan.TownsAndNations.utils;

import org.tan.TownsAndNations.DataClass.ClaimedChunk;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.storage.Legacy.ClaimedChunkStorage;
import org.tan.TownsAndNations.storage.DataStorage.NewClaimedChunkStorage;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;

import java.util.Iterator;
import java.util.Map;

public class UpdateUtil {

    public static void update(){
        updateNewChunkData();
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
