package org.leralix.tan.utils.text;

public class DateUtil {

    private DateUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static String getDateStringFromTicks(long date){
        int nbHours = (int) (date / 72000);
        int nbMinutes = (int) ((date % 72000) / 1200);
        return nbHours + "h" + String.format("%02d", nbMinutes) + "m";
    }
}
