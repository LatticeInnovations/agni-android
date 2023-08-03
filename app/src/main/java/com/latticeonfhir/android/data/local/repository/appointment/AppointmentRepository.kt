package com.latticeonfhir.android.data.local.repository.appointment

import com.latticeonfhir.android.data.server.model.scheduleandappointment.appointment.AppointmentResponse

interface AppointmentRepository {
    suspend fun getAppointmentListByDate(startOfDay: Long, endOfDay: Long): List<AppointmentResponse>
    suspend fun addAppointment(appointmentResponse: AppointmentResponse): List<Long>
    suspend fun updateAppointment(appointmentResponse: AppointmentResponse): Int
    suspend fun getAppointmentsOfPatientByStatus(
        patientId: String,
        status: String
    ): List<AppointmentResponse>
    suspend fun getAppointmentsOfPatientByDate(
        patientId: String,
        startOfDay: Long,
        endOfDay: Long
    ): AppointmentResponse?
}