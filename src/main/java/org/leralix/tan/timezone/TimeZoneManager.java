package org.leralix.tan.timezone;


import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;

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

    public String formatDateNowForPlayer(ITanPlayer tanPlayer){
        return formatDateForPlayer(tanPlayer, Instant.now());
    }

    public String formatDateForPlayer(ITanPlayer tanPlayer, Instant timestamp) {
        TimeZoneEnum timeZone = tanPlayer.getTimeZone();
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

    public TimeZoneEnum getTimezoneEnum() {
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

    public String getRelativeTimeDescription(LangType langType, long date) {
        Instant instant = Instant.ofEpochMilli(date);
        Instant now = Instant.now();

        long diffSeconds = now.getEpochSecond() - instant.getEpochSecond();


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

