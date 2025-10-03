package org.leralix.tan.war;

import org.jetbrains.annotations.NotNull;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.Range;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.timezone.TimeZoneManager;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class WarTimeSlot {

    private final List<Range> timeSlots;

    public WarTimeSlot(@NotNull List<String> allowedTimeSlotsWar){
        timeSlots = new ArrayList<>();

        for(String value : allowedTimeSlotsWar){
            timeSlots.add(extractTimeSlot(value));
        }
    }

    private Range extractTimeSlot(String value) {
        String[] timeSlots = value.split("-");
        if(timeSlots.length != 2){
            TownsAndNations.getPlugin().getLogger()
                    .warning("the time slot \"" + value + "\" is not valid. It will not be registered");
            return null;
        }
        try {
            int start = convertToMinutes(timeSlots[0].trim());
            int end = convertToMinutes(timeSlots[1].trim());

            return new Range(start, end);
        } catch (DateTimeParseException | NumberFormatException e) {
            TownsAndNations.getPlugin().getLogger()
                    .warning("The time slot \"" + value + "\" contains an invalid time format. It will be ignored.");
            return null;
        }
    }

    private int convertToMinutes(String time) throws DateTimeParseException {
        LocalTime localTime = LocalTime.parse(time);
        return localTime.getHour() * 60 + localTime.getMinute();
    }

    public List<String> getPrintedTimeSlots(LangType langType)
    {
        List<String> printedTime = new ArrayList<>();

        for(Range range : timeSlots){

            int hour1 = range.getMinVal() / 60;
            int minute1 = range.getMinVal() % 60;

            int hour2 = range.getMaxVal() / 60;
            int minute2 = range.getMaxVal() % 60;

            printedTime.add(Lang.AUTHORIZED_ATTACK_TIME_SLOT_SINGLE.get(langType,
                    Integer.toString(hour1),
                    Integer.toString(minute1),
                    Integer.toString(hour2),
                    Integer.toString(minute2)
            ));
        }

        return printedTime;
    }

    public List<Range> getTimeSlots() {
        return timeSlots;
    }

    public boolean canWarBeDeclared(Instant dateTime) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(dateTime, TimeZoneManager.getInstance().getTimezoneEnum().toZoneOffset());

        int currentMinutes = localDateTime.getHour() * 60 + localDateTime.getMinute();

        for (Range range : timeSlots) {
            if (currentMinutes >= range.getMinVal() && currentMinutes < range.getMaxVal()) {
                return true;
            }
        }
        return false;
    }
}
