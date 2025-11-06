package org.leralix.tan.utils.text;

import java.util.Random;
import org.leralix.lib.utils.RandomUtil;
import org.leralix.tan.utils.constants.Constants;

/** Utility class for handling strings */
public class StringUtil {
  private StringUtil() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Check if a string is a valid hex color code (ex: 00FF00 for green)
   *
   * @param colorCode The color code to check
   * @return True if the color code is a valid color, false otherwise
   */
  public static boolean isValidColorCode(String colorCode) {
    return colorCode.matches("^[0-9A-Fa-f]{6}$");
  }

  /**
   * Convert a hex color code to an integer
   *
   * @param hexColor The hex color code to convert
   * @return The integer representation of the hex color code
   */
  public static int hexColorToInt(String hexColor) {
    return Integer.parseInt(hexColor, 16);
  }

  public static int randomColor() {
    Random random = RandomUtil.getRandom();
    int red = random.nextInt(256);
    int green = random.nextInt(256);
    int blue = random.nextInt(256);

    return (red << 16) | (green << 8) | blue;
  }

  public static String getColoredMoney(double money) {
    String formatedMoney = formatMoney(money);
    if (money > 0) {
      return "§a+" + formatedMoney;
    } else if (money < 0) {
      return "§c" + formatedMoney;
    }
    return "§7" + formatedMoney;
  }

  public static String formatMoney(double amount) {

    if (amount < 1_000) {
      return Double.toString(handleDigits(amount));
    } else if (amount < 1_000_000) {
      return String.format("%.1fK", amount / 1_000);
    } else if (amount < 1_000_000_000) {
      return String.format("%.1fM", amount / 1_000_000);
    } else if (amount < 1_000_000_000_000L) {
      return String.format("%.1fB", amount / 1_000_000_000);
    } else {
      return String.format("%.1fT", amount / 1_000_000_000_000L);
    }
  }

  public static double handleDigits(double amount) {
    double digitVal = Math.pow(10, Constants.getNbDigits());
    return Math.round(amount * digitVal) / digitVal;
  }
}
