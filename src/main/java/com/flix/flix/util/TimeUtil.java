package com.flix.flix.util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class TimeUtil {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public static Long stringToLongTimeMinutes(String timeString) {
        LocalTime localTime = LocalTime.parse(timeString, FORMATTER);

        if (localTime.isAfter(LocalTime.MAX) || localTime.isBefore(LocalTime.MIN)) {
            throw new IllegalArgumentException("Invalid time: " + timeString);
        }

        long minutesSinceMidnight = localTime.toSecondOfDay() / 60;
        return minutesSinceMidnight;
    }

    public static String longToStringTime(Long minutes) {
        if (minutes == null || minutes < 0 || minutes >= 24 * 60) {
            throw new IllegalArgumentException("Invalid minutes: " + minutes);
        }

        LocalTime time = LocalTime.MIDNIGHT.plus(minutes, ChronoUnit.MINUTES);
        return FORMATTER.format(time);
    }

}
