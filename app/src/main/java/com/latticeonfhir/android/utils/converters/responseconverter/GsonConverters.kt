package com.latticeonfhir.android.utils.converters.responseconverter

import com.google.gson.reflect.TypeToken
import com.latticeonfhir.android.FhirApp.Companion.gson

object GsonConverters {

    inline fun <reified T> T.toJson(): String {
        val type = object : TypeToken<T>() {}.type
        return gson.toJson(this, type)
    }

    fun <T> String.fromJson(): T {
        val type = object : TypeToken<T>() {}.type
        return gson.fromJson(this, type)
    }

    fun <T> Map<*, Any?>?.mapToObject(type: Class<T>): T? {
        if (this == null) return null

        val json = gson.toJson(this)
        return gson.fromJson(json, type)
    }
}