package org.leralix.tan.utils;

import java.util.Random;

/**
 * Utility class for handling strings
 *
 */
public class StringUtil {
    private StringUtil() {
        throw new IllegalStateException("Utility class");
    }
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

    public static int randomColor() {
        Random random = RandomUtil.getRandom();
        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);

        return  (red << 16) | (green << 8) | blue;
    }

}
