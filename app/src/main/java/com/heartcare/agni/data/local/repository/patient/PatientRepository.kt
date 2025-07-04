package com.heartcare.agni.data.local.repository.patient

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.heartcare.agni.data.server.model.patient.PatientResponse

interface PatientRepository {

    suspend fun addPatient(patientResponse: PatientResponse): List<Long>
    suspend fun getPatientList(): LiveData<PagingData<PatientResponse>>
    suspend fun updatePatientData(patientResponse: PatientResponse): Int
    suspend fun getPatientById(vararg patientId: String): List<PatientResponse>
}