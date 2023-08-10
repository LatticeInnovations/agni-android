package com.latticeonfhir.android.data.local.repository.patient

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.latticeonfhir.android.data.server.model.patient.PatientResponse

interface PatientRepository {

    suspend fun addPatient(patientResponse: PatientResponse): List<Long>
    suspend fun getPatientList(): LiveData<PagingData<PatientResponse>>
    suspend fun updatePatientData(patientResponse: PatientResponse): Int
    suspend fun getPatientById(vararg patientId: String): List<PatientResponse>
}