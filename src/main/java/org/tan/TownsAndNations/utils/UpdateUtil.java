package org.tan.TownsAndNations.utils;

import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.storage.DataStorage.PlayerDataStorage;

/**
 * This class is used to update the plugin from old systems to new.
 */

public class UpdateUtil {

    public static void update(){
        if(!PlayerDataStorage.getLists().isEmpty())
            return;
        for(PlayerData playerData : PlayerDataStorage.getOldLists()){
            PlayerDataStorage.createPlayerDataClass(playerData);
        }
        PlayerDataStorage.saveStats();
    }



}
