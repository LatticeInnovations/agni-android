package com.latticeonfhir.android.auth.utils.converters.gson;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateSerializer implements JsonSerializer<Date> {

    @NonNull
    @Override
    public JsonElement serialize(@NonNull Date src, @NonNull Type typeOfSrc, @NonNull JsonSerializationContext context) {
        SimpleDateFormat formatter;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault());
            return new JsonPrimitive(formatter.format(src.getTime()));
        } else {
            formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sszzz", Locale.getDefault());
            return new JsonPrimitive(formatter.format(src.getTime()).replace("GMT", ""));
        }
    }
}
