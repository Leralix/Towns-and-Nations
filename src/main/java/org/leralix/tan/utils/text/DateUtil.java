package org.leralix.tan.utils.text;

import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;

public class DateUtil {

    private DateUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static String getDateStringFromMilli(long date){
        return getDateStringFromSeconds(date / 1000);
    }

    public static String getDateStringFromSeconds(long date){
        return getDateStringFromMinutes(date / 60);
    }

    public static String getDateStringFromMinutes(long date){
        int nbHours = (int) (date / 60);
        int nbMinutes = (int) (date % 60);
        return nbHours + "h" + String.format("%02d", nbMinutes) + "m";
    }

    public static String getRelativeTimeDescription(LangType langType, long date) {

        long diffSeconds = (System.currentTimeMillis() - date) / 1000;

        if (diffSeconds < 0) {
            return Lang.RELATIVE_IN_FUTURE.get(langType);
        }

        long minutes = diffSeconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        long months = days / 30;
        long years = days / 365;

        if (diffSeconds < 60) {
            return Lang.RELATIVE_TIME_SECOND.get(langType, String.valueOf(diffSeconds));
        } else if (minutes < 60) {
            return Lang.RELATIVE_TIME_MINUTE.get(langType, String.valueOf(minutes));
        } else if (hours < 24) {
            return Lang.RELATIVE_TIME_HOUR.get(langType, String.valueOf(hours));
        } else if (days < 7) {
            return Lang.RELATIVE_TIME_DAY.get(langType, String.valueOf(days));
        } else if (months < 12) {
            return Lang.RELATIVE_TIME_MONTH.get(langType, String.valueOf(months));
        } else {
            return Lang.RELATIVE_TIME_YEAR.get(langType, String.valueOf(years));
        }
    }
}
