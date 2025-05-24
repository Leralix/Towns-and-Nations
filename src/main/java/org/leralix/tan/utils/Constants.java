package org.leralix.tan.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;

public class Constants {


    private static boolean displayTerritoryColor;
    private static boolean enableNation;
    private static boolean enableRegion;

    public static void init(){

        FileConfiguration config = ConfigUtil.getCustomConfig(ConfigTag.MAIN);

        displayTerritoryColor = config.getBoolean("displayTerritoryNameWithOwnColor", false);
        enableNation = config.getBoolean("EnableKingdom",true);
        enableRegion = config.getBoolean("EnableRegion", true);
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
}
