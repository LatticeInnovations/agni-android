package com.heartcare.agni.data.server.repository.symptomsanddiagnosis

import com.heartcare.agni.data.server.model.symptomsanddiagnosis.Diagnosis
import com.heartcare.agni.data.server.model.symptomsanddiagnosis.Symptoms
import com.heartcare.agni.data.server.model.symptomsanddiagnosis.SymptomsAndDiagnosisItem
import com.heartcare.agni.data.server.model.symptomsanddiagnosis.SymptomsItem
import com.heartcare.agni.utils.converters.server.responsemapper.ResponseMapper

interface SymptomsAndDiagnosisRepository {


    suspend fun insertSymptoms(): ResponseMapper<List<Symptoms>>
    suspend fun insertDiagnosis(): ResponseMapper<List<Diagnosis>>

    suspend fun getSymptoms(): List<SymptomsItem>
    suspend fun getDiagnosis(): List<SymptomsAndDiagnosisItem>
}