package com.software.software_program.core.utils;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public final class Formatter {
    private Formatter() {
    }

    private static final DateTimeFormatter isoFormatter = DateTimeFormatter.ISO_INSTANT;

    public static String format(Date date) {
        return isoFormatter.format(date.toInstant());
    }

    public static Date parse(String date) {
        Instant instant = Instant.parse(date);
        return Date.from(instant);
    }
}