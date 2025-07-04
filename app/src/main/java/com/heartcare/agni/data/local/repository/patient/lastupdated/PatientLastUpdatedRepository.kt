package com.heartcare.agni.data.local.repository.patient.lastupdated

import com.heartcare.agni.data.server.model.patient.PatientLastUpdatedResponse

interface PatientLastUpdatedRepository {

    suspend fun insertPatientLastUpdatedData(patientLastUpdatedResponse: PatientLastUpdatedResponse): Long
}