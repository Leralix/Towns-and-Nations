package org.leralix.tan.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;

public class Constants {


    private static boolean displayTerritoryColor;
    private static boolean enableNation;
    private static boolean enableRegion;
    private static int changeTownNameCost;
    private static int changeRegionNameCost;
    private static int nbDigits;


    public static void init(){

        FileConfiguration config = ConfigUtil.getCustomConfig(ConfigTag.MAIN);

        displayTerritoryColor = config.getBoolean("displayTerritoryNameWithOwnColor", false);
        enableNation = config.getBoolean("EnableKingdom",true);
        enableRegion = config.getBoolean("EnableRegion", true);
        changeTownNameCost = config.getInt("changeTownNameCost", 1000);
        changeRegionNameCost = config.getInt("changeRegionNameCost", 1000);
        nbDigits = config.getInt("DecimalDigits",2);
    }

    public static boolean displayTerritoryColor(){
        return displayTerritoryColor;
    }

    public static boolean enableNation() {
        return enableNation;
    }

    public static boolean enableRegion() {
        return enableRegion;
    }

    public static int getChangeTerritoryNameCost(TerritoryData territoryData) {
        if(territoryData instanceof TownData){
            return changeTownNameCost;
        }
        if(territoryData instanceof RegionData){
            return changeRegionNameCost;
        }
        return changeTownNameCost;
    }

    public static int getNbDigits(){
        return nbDigits;
    }
}
