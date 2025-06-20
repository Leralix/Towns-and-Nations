package org.leralix.tan.timezone;


import org.leralix.tan.dataclass.PlayerData;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class TimeZoneManager {

    private static TimeZoneManager instance;
    private TimeZoneEnum timeZoneEnum;

    private TimeZoneManager() {
        this.timeZoneEnum = getServerTimeZone();
    }

    public static TimeZoneManager getInstance() {
        if (instance == null) {
            instance = new TimeZoneManager();
        }
        return instance;
    }

    private TimeZoneEnum getServerTimeZone() {
        ZoneOffset offset = ZonedDateTime.now().getOffset();
        return TimeZoneEnum.fromOffset(offset.getTotalSeconds() / 3600);
    }

    public String formatDateForPlayer(PlayerData playerData, Instant timestamp) {

        TimeZoneEnum timeZone = playerData.getTimeZone();
        ZonedDateTime zonedDateTime = timestamp.atZone(timeZone.toZoneOffset());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM HH:mm").withLocale(Locale.ENGLISH);
        return formatter.format(zonedDateTime);
    }

    public String formatDateForServer(Instant timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM HH:mm").withLocale(Locale.ENGLISH);
        return formatter.format(timeZoneEnum.toZoneOffset());
    }
}

