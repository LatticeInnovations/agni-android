package com.heartcare.agni.data.local.repository.identifier

import com.heartcare.agni.data.server.model.patient.PatientIdentifier
import com.heartcare.agni.data.server.model.patient.PatientResponse

interface IdentifierRepository {

    suspend fun insertIdentifierList(patientResponse: PatientResponse)
    suspend fun deleteIdentifier(vararg patientIdentifier: PatientIdentifier, patientId: String)

}