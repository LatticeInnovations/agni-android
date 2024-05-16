package com.latticeonfhir.android.data.local.repository.patient.lastupdated

import com.latticeonfhir.android.data.server.model.patient.PatientLastUpdatedResponse

interface PatientLastUpdatedRepository {

    suspend fun insertPatientLastUpdatedData(patientLastUpdatedResponse: PatientLastUpdatedResponse): Long
}