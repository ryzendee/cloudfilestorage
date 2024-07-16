package com.app.cloudfilestorage.utils;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateFormatterUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    
    public static String formatZonedDateTime(ZonedDateTime zonedDateTime) {
        return zonedDateTime.format(FORMATTER);
    }
}
