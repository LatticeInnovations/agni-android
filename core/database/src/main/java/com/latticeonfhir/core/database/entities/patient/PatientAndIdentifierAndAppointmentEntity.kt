package com.latticeonfhir.core.database.entities.patient

import androidx.annotation.Keep
import com.latticeonfhir.core.database.entities.appointment.AppointmentEntity

@Keep
data class PatientAndIdentifierAndAppointmentEntity(
    val patientAndIdentifierEntity: PatientAndIdentifierEntity,
    val appointmentEntity: AppointmentEntity
)