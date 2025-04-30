package com.latticeonfhir.core.data.repository.local.identifier

import com.latticeonfhir.core.model.server.patient.PatientIdentifier
import com.latticeonfhir.core.model.server.patient.PatientResponse

interface IdentifierRepository {

    suspend fun insertIdentifierList(patientResponse: PatientResponse)
    suspend fun deleteIdentifier(vararg patientIdentifier: PatientIdentifier, patientId: String)

}