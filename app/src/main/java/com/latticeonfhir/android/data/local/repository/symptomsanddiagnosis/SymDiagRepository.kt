package com.latticeonfhir.android.data.local.repository.symptomsanddiagnosis

import com.latticeonfhir.android.data.local.roomdb.entities.symptomsanddiagnosis.SymptomsAndDiagnosisLocal

interface SymDiagRepository {

    suspend fun insertSymptomsAndDiagnosis( local: SymptomsAndDiagnosisLocal): List<Long>


    suspend fun getPastSymptomsAndDiagnosis(patientId: String
    ): List<SymptomsAndDiagnosisLocal>

    suspend fun updateSymDiagFhirId(symDiagUuid: String, fhirId: String)

    suspend fun getSymDiagByAppointmentId(appointmentId: String): List<SymptomsAndDiagnosisLocal>

    suspend fun updateSymDiagData(symptomsAndDiagnosisLocal: SymptomsAndDiagnosisLocal): Int
}