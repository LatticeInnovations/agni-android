package com.latticeonfhir.android.utils.converters.responseconverter

import com.google.gson.reflect.TypeToken
import com.latticeonfhir.android.FhirApp

object GsonConverters {

    inline fun <reified T> T.toJson(): String {
        val type = object : TypeToken<T>() {}.type
        return FhirApp.gson.toJson(this,type)
    }

    fun <T> String.fromJson(): T {
        val type = object : TypeToken<T>() {}.type
        return FhirApp.gson.fromJson(this,type)
    }
}