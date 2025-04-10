package com.latticeonfhir.core.data.repository.local.appointment

import com.latticeonfhir.core.database.entities.appointment.AppointmentEntity
import com.latticeonfhir.core.model.local.appointment.AppointmentResponseLocal

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

    suspend fun getAppointmentByAppointmentId(appointmentId: String): AppointmentResponseLocal

    suspend fun getLastCompletedAppointment(
        patientId: String
    ): AppointmentEntity?
}