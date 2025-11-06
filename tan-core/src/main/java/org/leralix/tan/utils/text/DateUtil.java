package org.leralix.tan.utils.text;

public class DateUtil {

  private DateUtil() {
    throw new IllegalStateException("Utility class");
  }

  public static String getDateStringFromTicks(long date) {
    int nbHours = (int) (date / 60);
    int nbMinutes = (int) (date % 60);
    return nbHours + "h" + String.format("%02d", nbMinutes) + "m";
  }
}
