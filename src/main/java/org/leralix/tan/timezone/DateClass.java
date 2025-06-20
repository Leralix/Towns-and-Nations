package org.leralix.tan.timezone;

import org.leralix.tan.lang.Lang;

import java.time.ZonedDateTime;

public class DateClass {

    private final long timestamp;

    public DateClass(ZonedDateTime zonedDateTime) {
        this.timestamp = zonedDateTime.toInstant().toEpochMilli();
    }

    public String formatDate() {

        int month = (int) ((timestamp % 31536000000L) / 2592000000L);
        int day = (int) ((timestamp % 2592000000L) / 86400000L);
        int hours = (int) ((timestamp % 86400000L) / 3600000L);
        int minutes = (int) ((timestamp % 3600000L) / 60000L);

        return Lang.DATE_AND_TIME_FORMAT.get(day, month, hours, minutes);
    }
}
