package org.tan.TownsAndNations.utils;

public class DateUtil {

    public static String getStringDeltaDateTime(long date){
        int nbHours = (int) (date / 72000);
        int nbMinutes = (int) ((date % 72000) / 1200);
        return nbHours + "h" + String.format("%02d", nbMinutes) + "m";
    }
}
