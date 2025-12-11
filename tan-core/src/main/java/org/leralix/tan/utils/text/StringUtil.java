package org.leralix.tan.utils.text;

import java.util.Random;
import org.leralix.lib.utils.RandomUtil;
import org.leralix.tan.utils.constants.Constants;

public class StringUtil {
  private StringUtil() {
    throw new IllegalStateException("Utility class");
  }

  public static boolean isValidColorCode(String colorCode) {
    return colorCode.matches("^[0-9A-Fa-f]{6}$");
  }

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
