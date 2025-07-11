package com.heartcare.agni.utils.converters.responseconverter

import java.util.Locale

object StringUtils {

    fun String.capitalizeFirst(): String = replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
    }
}