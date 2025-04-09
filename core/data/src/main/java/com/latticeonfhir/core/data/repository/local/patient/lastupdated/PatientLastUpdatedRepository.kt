package com.latticeonfhir.core.data.repository.local.patient.lastupdated

import com.latticeonfhir.core.data.server.model.patient.PatientLastUpdatedResponse

interface PatientLastUpdatedRepository {

    suspend fun insertPatientLastUpdatedData(patientLastUpdatedResponse: PatientLastUpdatedResponse): Long
}