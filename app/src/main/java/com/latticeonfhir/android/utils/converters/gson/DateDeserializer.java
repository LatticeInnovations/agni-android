package com.latticeonfhir.android.utils.converters.gson;

import androidx.annotation.NonNull;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import timber.log.Timber;

public class DateDeserializer implements JsonDeserializer<Date> {
    private static final String[] DATE_FORMATS = new String[]{
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS'Z'",
            "yyyy-MM-dd HH:mm:ssZ",
            "yyyy-MM-dd'T'HH:mm:ssZ",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd",
            "yyyy-MM-dd'T'HH:mm:ssXXX",
            "yyyy-MM-dd'T'HH:mm:sszzz"
    };

    @NonNull
    @Override
    public Date deserialize(@NonNull JsonElement jsonElement, @NonNull Type typeOF, @NonNull JsonDeserializationContext context) throws JsonParseException {
        for (String format : DATE_FORMATS) {
            try {
                return Objects.requireNonNull(new SimpleDateFormat(format, Locale.getDefault()).parse(jsonElement.getAsString()));
            } catch (ParseException e) {
                Timber.e("ParseException");
            }
        }
        throw new JsonParseException("Unparseable date: \"" + jsonElement.getAsString()
                + "\". Supported formats: " + Arrays.toString(DATE_FORMATS));
    }
}
