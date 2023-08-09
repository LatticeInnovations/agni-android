package com.latticeonfhir.android.data.local.repository.appointment

import com.latticeonfhir.android.data.local.model.appointment.AppointmentResponseLocal

interface AppointmentRepository {
    suspend fun getAppointmentListByDate(startOfDay: Long, endOfDay: Long): List<AppointmentResponseLocal>
    suspend fun addAppointment(appointmentResponseLocal: AppointmentResponseLocal): List<Long>
    suspend fun updateAppointment(appointmentResponseLocal: AppointmentResponseLocal): Int
    suspend fun getAppointmentsOfPatientByStatus(
        patientId: String,
        status: String
    ): List<AppointmentResponseLocal>
    suspend fun getAppointmentsOfPatientByDate(
        patientId: String,
        startOfDay: Long,
        endOfDay: Long
    ): AppointmentResponseLocal?
}