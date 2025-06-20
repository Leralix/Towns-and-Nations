package org.leralix.tan.timezone;


import java.time.ZoneOffset;

public enum TimeZoneEnum {
    UTC_MINUS_12(-12),
    UTC_MINUS_11(-11),
    UTC_MINUS_10(-10),
    UTC_MINUS_9(-9),
    UTC_MINUS_8(-8),
    UTC_MINUS_7(-7),
    UTC_MINUS_6(-6),
    UTC_MINUS_5(-5),
    UTC_MINUS_4(-4),
    UTC_MINUS_3(-3),
    UTC_MINUS_2(-2),
    UTC_MINUS_1(-1),
    UTC(0),
    UTC_PLUS_1(1),
    UTC_PLUS_2(2),
    UTC_PLUS_3(3),
    UTC_PLUS_4(4),
    UTC_PLUS_5(5),
    UTC_PLUS_6(6),
    UTC_PLUS_7(7),
    UTC_PLUS_8(8),
    UTC_PLUS_9(9),
    UTC_PLUS_10(10),
    UTC_PLUS_11(11),
    UTC_PLUS_12(12);

    private final int offset;

    TimeZoneEnum(int offset) {
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
}
