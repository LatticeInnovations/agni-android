package com.latticeonfhir.android.data.local.repository.identifier

import com.latticeonfhir.android.data.server.model.patient.PatientResponse

interface IdentifierRepository {

    suspend fun insertIdentifierList(patientResponse: PatientResponse)

}