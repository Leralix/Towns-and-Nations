package org.leralix.tan.timezone;


import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;

import java.time.ZoneOffset;

public enum TimeZoneEnum {
    UTC_MINUS_12(Lang.UTC_MINUS_12, -12),
    UTC_MINUS_11(Lang.UTC_MINUS_11, -11),
    UTC_MINUS_10(Lang.UTC_MINUS_10, -10),
    UTC_MINUS_9(Lang.UTC_MINUS_9, -9),
    UTC_MINUS_8(Lang.UTC_MINUS_8, -8),
    UTC_MINUS_7(Lang.UTC_MINUS_7, -7),
    UTC_MINUS_6(Lang.UTC_MINUS_6, -6),
    UTC_MINUS_5(Lang.UTC_MINUS_5, -5),
    UTC_MINUS_4(Lang.UTC_MINUS_4, -4),
    UTC_MINUS_3(Lang.UTC_MINUS_3, -3),
    UTC_MINUS_2(Lang.UTC_MINUS_2, -2),
    UTC_MINUS_1(Lang.UTC_MINUS_1, -1),
    UTC(Lang.UTC, 0),
    UTC_PLUS_1(Lang.UTC_PLUS_1, 1),
    UTC_PLUS_2(Lang.UTC_PLUS_2, 2),
    UTC_PLUS_3(Lang.UTC_PLUS_3, 3),
    UTC_PLUS_4(Lang.UTC_PLUS_4, 4),
    UTC_PLUS_5(Lang.UTC_PLUS_5, 5),
    UTC_PLUS_6(Lang.UTC_PLUS_6, 6),
    UTC_PLUS_7(Lang.UTC_PLUS_7, 7),
    UTC_PLUS_8(Lang.UTC_PLUS_8, 8),
    UTC_PLUS_9(Lang.UTC_PLUS_9, 9),
    UTC_PLUS_10(Lang.UTC_PLUS_10, 10),
    UTC_PLUS_11(Lang.UTC_PLUS_11, 11),
    UTC_PLUS_12(Lang.UTC_PLUS_12, 12);

    private final Lang name;
    private final int offset;

    TimeZoneEnum(Lang name, int offset) {
        this.name = name;
        this.offset = offset;
    }

    public ZoneOffset toZoneOffset() {
        return ZoneOffset.ofHours(offset);
    }

    public static TimeZoneEnum fromOffset(int offset) {
        for (TimeZoneEnum zone : values()) {
            if (zone.offset == offset) return zone;
        }
        throw new IllegalArgumentException("Invalid UTC offset: " + offset);
    }

    public String getName(LangType langType){
        return name.get(langType);
    }
}
