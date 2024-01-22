package org.tan.TownsAndNations.utils;

import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.DataClass.TownRank;
import org.tan.TownsAndNations.enums.TownRankEnum;
import org.tan.TownsAndNations.storage.TownDataStorage;

public class UpdateUtil {

    public static void update(){
        UpdateRankEnum();
    }
    private static void UpdateRankEnum(){
        for(TownData town : TownDataStorage.getTownList().values()) {

            for (TownRank townRank : town.getTownRanks()) {

                if (townRank.getRankEnum() == null) {
                    townRank.setRankEnum(town.getID(), TownRankEnum.FIVE);
                }
            }

        }
    }
}
