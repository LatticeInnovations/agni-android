package com.latticeonfhir.android.data.local.repository.patient

import com.latticeonfhir.android.data.server.model.PatientResponse

interface PatientRepository {

    suspend fun getPatientList(): List<PatientResponse>
    suspend fun updatePatientData(patientResponse: PatientResponse): Int
}