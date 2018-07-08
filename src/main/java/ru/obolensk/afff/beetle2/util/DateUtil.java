package ru.obolensk.afff.beetle2.util;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import javax.annotation.Nonnull;

/**
 * Created by Afff on 09.06.2016.
 */
public class DateUtil {

    @Nonnull
    public static String getHttpTime() {
        final Instant instant = Instant.now();
        return DateTimeFormatter.RFC_1123_DATE_TIME
                .withZone(ZoneOffset.UTC)
                .format(instant);
    }
}
