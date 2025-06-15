package org.leralix.tan.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.dataclass.chunk.ChunkType;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
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
    private static boolean worldGuardOverrideWilderness;
    private static boolean worldGuardOverrideTown;
    private static boolean worldGuardOverrideRegion;
    private static boolean worldGuardOverrideLandmark;


    public static void init(){

        FileConfiguration config = ConfigUtil.getCustomConfig(ConfigTag.MAIN);

        displayTerritoryColor = config.getBoolean("displayTerritoryNameWithOwnColor", false);
        enableNation = config.getBoolean("EnableKingdom",true);
        enableRegion = config.getBoolean("EnableRegion", true);
        changeTownNameCost = config.getInt("changeTownNameCost", 1000);
        changeRegionNameCost = config.getInt("changeRegionNameCost", 1000);
        nbDigits = config.getInt("DecimalDigits",2);
        worldGuardOverrideWilderness = config.getBoolean("worldguard_override_wilderness", true);
        worldGuardOverrideTown = config.getBoolean("worldguard_override_town", true);
        worldGuardOverrideRegion = config.getBoolean("worldguard_override_region", true);
        worldGuardOverrideLandmark = config.getBoolean("worldguard_override_landmark", true);
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

    public static boolean isWorldGuardEnabledFor(ChunkType chunkType){
        return switch (chunkType) {
            case WILDERNESS -> worldGuardOverrideWilderness;
            case TOWN -> worldGuardOverrideTown;
            case REGION -> worldGuardOverrideRegion;
            case LANDMARK -> worldGuardOverrideLandmark;
        };
    }
}
