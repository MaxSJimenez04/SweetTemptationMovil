package com.example.sweettemptation.network;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.ToJson;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public final class BigDecimalJsonAdapter {

    private static final int SCALE = 2;      // (9,2) => 2 decimales
    private static final int PRECISION = 9;  // 9 dígitos totales

    @FromJson
    public BigDecimal fromJson(JsonReader reader) throws IOException {
        JsonReader.Token token = reader.peek();

        if (token == JsonReader.Token.NULL) {
            reader.nextNull();
            return null;
        }

        if (token == JsonReader.Token.NUMBER || token == JsonReader.Token.STRING) {
            String raw = reader.nextString();
            if (raw == null || raw.isBlank()) return null;

            try {
                BigDecimal bd = new BigDecimal(raw).setScale(SCALE, RoundingMode.HALF_UP);

                if (bd.precision() > PRECISION) {
                    throw new JsonDataException("BigDecimal fuera de rango DECIMAL(9,2): " + bd);
                }

                return bd;
            } catch (NumberFormatException e) {
                throw new JsonDataException("BigDecimal inválido: '" + raw + "'", e);
            }
        }

        throw new JsonDataException("Tipo JSON inválido para BigDecimal: " + token);
    }

    @ToJson
    public void toJson(JsonWriter writer, BigDecimal value) throws IOException {
        if (value == null) {
            writer.nullValue();
            return;
        }

        BigDecimal bd = value.setScale(SCALE, RoundingMode.HALF_UP);

        if (bd.precision() > PRECISION) {
            throw new JsonDataException("BigDecimal fuera de rango DECIMAL(9,2): " + bd);
        }

        writer.value(bd);
    }
}
