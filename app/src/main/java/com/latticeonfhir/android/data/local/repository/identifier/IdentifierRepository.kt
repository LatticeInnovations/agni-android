package com.latticeonfhir.android.data.local.repository.identifier

import com.latticeonfhir.android.data.local.roomdb.entities.patient.IdentifierEntity
import com.latticeonfhir.android.data.server.model.patient.PatientIdentifier
import com.latticeonfhir.android.data.server.model.patient.PatientResponse

interface IdentifierRepository {

    suspend fun insertIdentifierList(patientResponse: PatientResponse)
    suspend fun deleteIdentifier(vararg patientIdentifier: PatientIdentifier, patientId: String)

}