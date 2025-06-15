package com.software.software_program.core.utility;

import lombok.experimental.UtilityClass;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
@UtilityClass
public final class Formatter {

    private static final DateTimeFormatter isoFormatter = DateTimeFormatter.ISO_INSTANT;

    private static final DateTimeFormatter customDateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    public static String format(Date date) {
        if (date == null) {
            return null;
        }
        return isoFormatter.format(date.toInstant());
    }

    public static String formatToCustomString(Date date) {
        if (date == null) {
            return null;
        }
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return localDateTime.format(customDateTimeFormatter);
    }

    public static Date parse(String date) {
        try {
            // Пробуем ISO INSTANT
            return Date.from(Instant.parse(date));
        } catch (DateTimeParseException e1) {
            try {
                // Пробуем yyyy-MM-dd
                LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            } catch (DateTimeParseException e2) {
                throw new IllegalArgumentException("Invalid date format: " + date);
            }
        }
    }

    public static String formatToCustomString(LocalDate date) {
        if (date == null) return null;
        return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }
}