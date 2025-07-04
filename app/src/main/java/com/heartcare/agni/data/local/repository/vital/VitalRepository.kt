package com.heartcare.agni.data.local.repository.vital

import com.heartcare.agni.data.local.model.vital.VitalLocal

interface VitalRepository {

    suspend fun insertVital(vitalLocal: VitalLocal): Long
    suspend fun getLastVital(patientId: String): List<VitalLocal>
    suspend fun getVitalByAppointmentId(appointmentId: String): List<VitalLocal>
    suspend fun updateVital(vitalLocal: VitalLocal): Int


}