package com.latticeonfhir.core.data.repository.local.patient.lastupdated

import com.latticeonfhir.core.model.server.patient.PatientLastUpdatedResponse

interface PatientLastUpdatedRepository {

    suspend fun insertPatientLastUpdatedData(patientLastUpdatedResponse: PatientLastUpdatedResponse): Long
}