package com.latticeonfhir.core.network.utils.gson

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.util.Date


val gson: Gson =
        GsonBuilder()
            .registerTypeAdapter(Date::class.java, DateDeserializer())
            .registerTypeAdapter(Date::class.java, DateSerializer())
            .create()
