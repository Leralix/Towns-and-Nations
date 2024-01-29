package org.tan.TownsAndNations.utils;

import net.md_5.bungee.api.ChatColor;

public class StringUtil {

    public static boolean isValidColorCode(String colorCode) {
        String regexWith0x = "^0x[0-9A-Fa-f]{6}$";
        String regexWithoutOx = "^[0-9A-Fa-f]{6}$";

        return (colorCode.matches(regexWith0x) || colorCode.matches(regexWithoutOx));
    }

    public static int hexColorToInt(String hexColor) {
        if (hexColor.startsWith("0x")) {
            hexColor = hexColor.substring(2);
        }
        return Integer.parseInt(hexColor, 16);
    }


    public static ChatColor getHexColor(String hexaColor){
        return net.md_5.bungee.api.ChatColor.of(hexaColor);
    }

}
