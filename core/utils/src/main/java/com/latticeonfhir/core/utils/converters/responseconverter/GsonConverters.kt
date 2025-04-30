package com.latticeonfhir.core.utils.converters.responseconverter

import com.google.gson.reflect.TypeToken
import com.latticeonfhir.core.utils.converters.gson.gson

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
    inline fun <reified T> deserializeList(input: List<*>?): List<T>? {
        if (input == null) return null

        val json = gson.toJson(input)
        val type = object : TypeToken<List<T>>() {}.type

        return gson.fromJson(json, type)
    }
}