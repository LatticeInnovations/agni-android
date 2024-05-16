package com.latticeonfhir.android.data.local.repository.patient.lastupdated

import com.latticeonfhir.android.data.local.roomdb.entities.patient.PatientLastUpdatedEntity

interface PatientLastUpdatedRepository {

    suspend fun insertPatientLastUpdatedData(patientLastUpdatedEntity: PatientLastUpdatedEntity): Long
}