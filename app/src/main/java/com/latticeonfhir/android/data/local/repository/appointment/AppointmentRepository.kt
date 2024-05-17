package com.latticeonfhir.android.data.local.repository.appointment

import com.latticeonfhir.android.data.local.model.appointment.AppointmentResponseLocal
import com.latticeonfhir.android.data.local.roomdb.entities.appointment.AppointmentEntity

interface AppointmentRepository {
    suspend fun getAppointmentListByDate(
        startOfDay: Long,
        endOfDay: Long
    ): List<AppointmentResponseLocal>

    suspend fun addAppointment(appointmentResponseLocal: AppointmentResponseLocal): List<Long>
    suspend fun updateAppointment(appointmentResponseLocal: AppointmentResponseLocal): Int
    suspend fun getAppointmentsOfPatientByStatus(
        patientId: String,
        status: String
    ): List<AppointmentResponseLocal>

    suspend fun getAppointmentsOfPatient(
        patientId: String
    ): List<AppointmentResponseLocal>

    suspend fun getAppointmentsOfPatientByDate(
        patientId: String,
        startOfDay: Long,
        endOfDay: Long
    ): AppointmentResponseLocal?

    suspend fun getLastCompletedAppointment(
        patientId: String
    ): AppointmentEntity?
}