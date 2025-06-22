package org.leralix.tan.timezone;


import org.jetbrains.annotations.NotNull;
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

    public String formatDateNowForPlayer(PlayerData playerData){
        return formatDateForPlayer(playerData, Instant.now());
    }

    public String formatDateForPlayer(PlayerData playerData, Instant timestamp) {
        TimeZoneEnum timeZone = playerData.getTimeZone();
        return getDate(timestamp, timeZone);
    }

    public String formatDateNowForServer() {
        return formatDateForServer(Instant.now());
    }

    public String formatDateForServer(Instant timestamp) {
        return getDate(timestamp, getServerTimeZone());
    }

    private static @NotNull String getDate(Instant timestamp, TimeZoneEnum timeZone) {
        ZonedDateTime zonedDateTime = timestamp.atZone(timeZone.toZoneOffset());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM HH:mm").withLocale(Locale.ENGLISH);
        return formatter.format(zonedDateTime);
    }

    public TimeZoneEnum getServerTimezone() {
        return timeZoneEnum;
    }

    public void setTimeZoneEnum(TimeZoneEnum timeZoneEnum){
        this.timeZoneEnum = timeZoneEnum;
    }

    public boolean isDayForServer() {
        ZonedDateTime zonedDateTime = Instant.now().atZone(getServerTimeZone().toZoneOffset());
        int hourOfDay = zonedDateTime.getHour();
        return hourOfDay >= 8 && hourOfDay < 20;
    }
}

