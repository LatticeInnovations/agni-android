package com.latticeonfhir.android.data.server.repository.symptomsanddiagnosis

import com.latticeonfhir.android.data.server.model.symptomsanddiagnosis.Diagnosis
import com.latticeonfhir.android.data.server.model.symptomsanddiagnosis.Symptoms
import com.latticeonfhir.android.data.server.model.symptomsanddiagnosis.SymptomsAndDiagnosisItem
import com.latticeonfhir.android.data.server.model.symptomsanddiagnosis.SymptomsItem
import com.latticeonfhir.android.utils.converters.server.responsemapper.ResponseMapper

interface SymptomsAndDiagnosisRepository {


    suspend fun insertSymptoms(): ResponseMapper<List<Symptoms>>
    suspend fun insertDiagnosis(): ResponseMapper<List<Diagnosis>>

    suspend fun getSymptoms(): List<SymptomsItem>
    suspend fun getDiagnosis(): List<SymptomsAndDiagnosisItem>
}