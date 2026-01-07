package com.example.sweettemptation.network;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateJsonAdapter {

    private static final SimpleDateFormat sdf =
            new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    static {
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @FromJson
    Date fromJson(String value) throws ParseException {
        return sdf.parse(value);
    }

    @ToJson
    String toJson(Date value) {
        return sdf.format(value);
    }
}
