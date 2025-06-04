package com.software.software_program.core.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

public final class Formatter {
    private Formatter() {
    }

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
            Instant instant = Instant.parse(date);
            return Date.from(instant);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format: " + date);
        }
    }

    public static LocalDate parseToLocalDate(String date) {
        try {
            return LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format: " + date);
        }
    }


    public static String formatToCustomString(LocalDate date) {
        if (date == null) return null;
        return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }
}