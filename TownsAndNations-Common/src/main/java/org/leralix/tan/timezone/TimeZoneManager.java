package org.leralix.tan.timezone;


import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;

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

    public FilledLang formatDateNowForPlayer(ITanPlayer tanPlayer){
        return formatDateForPlayer(tanPlayer, Instant.now());
    }

    public FilledLang formatDateForPlayer(ITanPlayer tanPlayer, Instant timestamp) {
        return formatDate(timestamp, tanPlayer.getTimeZone(), tanPlayer.getLang().getLocale());
    }

    public FilledLang formatDate(Instant timestamp, TimeZoneEnum timeZone, Locale locale) {
        return getDate(timestamp, timeZone, locale);
    }

    public FilledLang formatDateNowForServer() {
        return formatDateForServer(Instant.now());
    }

    public FilledLang formatDateForServer(Instant timestamp) {
        return getDate(timestamp, getServerTimeZone(), Lang.getServerLang().getLocale());
    }

    private static FilledLang getDate(Instant timestamp, TimeZoneEnum timeZone, Locale locale) {
        ZonedDateTime zonedDateTime = timestamp.atZone(timeZone.toZoneOffset());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm").withLocale(locale);
        return Lang.STRING.get(formatter.format(zonedDateTime));
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
}

