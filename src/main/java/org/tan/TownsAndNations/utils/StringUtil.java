package org.tan.TownsAndNations.utils;

import net.md_5.bungee.api.ChatColor;

import java.util.Random;

/**
 * Utility class for handling strings
 *
 */
public class StringUtil {
    /**
     * Check if a string is a valid hex color code (ex: 00FF00 for green)
     * @param colorCode The color code to check
     * @return          True if the color code is a valid color, false otherwise
     */
    public static boolean isValidColorCode(String colorCode) {
        return colorCode.matches("^[0-9A-Fa-f]{6}$");
    }

    /**
     * Convert a hex color code to an integer
     * @param hexColor The hex color code to convert
     * @return         The integer representation of the hex color code
     */
    public static int hexColorToInt(String hexColor) {
        return Integer.parseInt(hexColor, 16);
    }

    /**
     * Get the {@link ChatColor} from a hex color code
     * @param hexaColor The color to get the hex code of
     * @return      The hex code of the color
     */
    public static ChatColor getHexColor(String hexaColor){
        return net.md_5.bungee.api.ChatColor.of(hexaColor);
    }

    public static int randomColor() {
        Random random = new Random();
        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);

        return  (red << 16) | (green << 8) | blue;
    }

}
