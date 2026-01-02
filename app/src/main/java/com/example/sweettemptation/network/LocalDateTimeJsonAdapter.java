package com.example.sweettemptation.network;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

public final class LocalDateTimeJsonAdapter {

    private static final DateTimeFormatter FMT = new DateTimeFormatterBuilder()
            .append(DateTimeFormatter.ISO_LOCAL_DATE)
            .appendLiteral('T')
            .appendValue(ChronoField.HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
            .appendLiteral(':')
            .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
            // 0 a 9 dígitos de fracción (nanos), con el punto opcional
            .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
            .toFormatter();

    @FromJson
    public LocalDateTime fromJson(String value) {
        if (value == null || value.isBlank()) return null;
        return LocalDateTime.parse(value, FMT);
    }

    @ToJson
    public String toJson(LocalDateTime value) {
        return value == null ? null : value.format(FMT);
    }
}
