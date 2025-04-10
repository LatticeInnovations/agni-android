package com.latticeonfhir.core.data.repository.local.patient

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.latticeonfhir.core.model.server.patient.PatientResponse

interface PatientRepository {

    suspend fun addPatient(patientResponse: PatientResponse): List<Long>
    suspend fun getPatientList(): LiveData<PagingData<PatientResponse>>
    suspend fun updatePatientData(patientResponse: PatientResponse): Int
    suspend fun getPatientById(vararg patientId: String): List<PatientResponse>
}