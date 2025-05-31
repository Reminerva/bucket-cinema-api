package com.flix.flix.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class DateUtilTest {

    @Test
    void testIsWeekendShouldSuccess() {

        LocalDate date = LocalDate.of(2025, 5, 31);
        boolean isWeekend = DateUtil.isWeekend(date);
        
        assertTrue(isWeekend);

        LocalDate date2 = LocalDate.of(2025, 5, 29);
        boolean isWeekend2 = DateUtil.isWeekend(date2);
        
        assertFalse(isWeekend2);

        LocalDate date3 = LocalDate.of(2025, 6, 1);
        boolean isWeekend3 = DateUtil.isWeekend(date3);
        
        assertTrue(isWeekend3);

    }

    @Test
    void testParseDateShouldSuccess() {
        String date = "2025-05-31";
        LocalDate parsedDate = DateUtil.parseDate(date);
        assertTrue(parsedDate.equals(LocalDate.of(2025, 5, 31)));
    }

    @Test
    void testParseDateTimeShouldSuccess() {
        String dateTime = "2025-05-31 12:00:00";
        LocalDateTime parsedDateTime = DateUtil.parseDateTime(dateTime);
        assertTrue(parsedDateTime.equals(LocalDateTime.of(2025, 5, 31, 12, 0, 0)));
    }

    @Test
    void testParseDateShouldThrowException() {
        String date = "2025/05/31";
        assertThrows(IllegalArgumentException.class, () -> DateUtil.parseDate(date));
    }

    @Test
    void testParseDateTimeShouldThrowException() {
        String dateTime = "2025/05/31 12:00:00";
        assertThrows(IllegalArgumentException.class, () -> DateUtil.parseDateTime(dateTime));
    }
}
