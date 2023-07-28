package com.latticeonfhir.android.data.local.repository.appointment

import com.latticeonfhir.android.data.local.roomdb.entities.appointment.AppointmentEntity
import com.latticeonfhir.android.data.server.model.scheduleandappointment.appointment.AppointmentResponse
import java.util.Date

interface AppointmentRepository {
    suspend fun getAppointmentListByDate(startOfDay: Date, endOfDay: Date): List<AppointmentEntity>
    suspend fun addAppointment(appointmentResponse: AppointmentResponse): List<Long>
    suspend fun updateAppointment(appointmentResponse: AppointmentResponse): Int
    suspend fun getAppointmentsOfPatientByStatus(
        patientId: String,
        status: String
    ): List<AppointmentEntity>
}