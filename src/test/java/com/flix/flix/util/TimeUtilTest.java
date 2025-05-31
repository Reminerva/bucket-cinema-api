package com.flix.flix.util;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class TimeUtilTest {
    @Test
    void testLongToStringTime_shouldSuccess() {
        long minutes = 60;
        String time = TimeUtil.longToStringTime(minutes);
        assert(time.equals("01:00"));
    }

    @Test
    void testLongToStringTime_shouldThrowException() {
        long minutes = -1;
        assertThrows(IllegalArgumentException.class, () -> TimeUtil.longToStringTime(minutes));

        long minutes2 = 24 * 60;
        assertThrows(IllegalArgumentException.class, () -> TimeUtil.longToStringTime(minutes2));

        assertThrows(IllegalArgumentException.class, () -> TimeUtil.longToStringTime(null));
    }

    @Test
    void testStringToLongTimeMinutes_shouldSuccess() {
        String time = "01:00";
        long minutes = TimeUtil.stringToLongTimeMinutes(time);
        assert(minutes == 60);
    }

    @Test
    void testStringToLongTimeMinutes_shouldThrowException() {
        
    }
}
