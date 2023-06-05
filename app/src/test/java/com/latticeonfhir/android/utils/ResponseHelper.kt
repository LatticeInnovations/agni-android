package com.latticeonfhir.android.utils

import java.io.InputStreamReader

object ResponseHelper {

    internal fun readJsonResponse(filename: String): String {
        val inputStream = ResponseHelper::class.java.getResourceAsStream(filename)
        val builder = StringBuilder()
        val reader = InputStreamReader(inputStream)
        reader.readLines().forEach {
            builder.append(it)
        }
        return builder.toString()
    }
}