package org.leralix.tan.utils;

import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;

public class Constants {


    private static boolean displayTerritoryColor;

    public static void init(){
        displayTerritoryColor = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getBoolean("displayTerritoryNameWithOwnColor", false);

    }

    public static boolean displayTerritoryColor(){
        return displayTerritoryColor;
    }




}
