package com.latticeonfhir.core.database.typeconverters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.latticeonfhir.core.model.server.symptomsanddiagnosis.SymptomsAndDiagnosisItem

class SymptomDiagnosisTypeConverter {

    @TypeConverter
    fun fromListToJson(list: List<SymptomsAndDiagnosisItem>?): String? {
        return if (list == null) null else Gson().toJson(list)
    }

    @TypeConverter
    fun fromJsonToList(json: String?): List<SymptomsAndDiagnosisItem>? {
        if (json == null) return null
        val type = object : TypeToken<List<SymptomsAndDiagnosisItem>>() {}.type
        return Gson().fromJson(json, type)
    }
}