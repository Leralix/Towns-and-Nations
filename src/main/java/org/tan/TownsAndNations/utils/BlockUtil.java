package org.tan.TownsAndNations.utils;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.metadata.FixedMetadataValue;
import org.tan.TownsAndNations.DataClass.PropertyData;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;

public class BlockUtil {

    public static void setSignData(){

        for( TownData townData : TownDataStorage.getTownMap().values() ){
            for( PropertyData propertyData : townData.getPropertyDataMap().values() ){
                Block block = propertyData.getSignLocation();
                block.setMetadata("propertySign", new FixedMetadataValue(TownsAndNations.getPlugin(), propertyData.getTotalID()));
            }
        }

    }

}
