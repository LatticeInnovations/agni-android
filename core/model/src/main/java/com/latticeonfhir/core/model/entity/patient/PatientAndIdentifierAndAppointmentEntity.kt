package com.latticeonfhir.core.model.entity.patient

import androidx.annotation.Keep
import com.latticeonfhir.core.model.entity.appointment.AppointmentEntity

@Keep
data class PatientAndIdentifierAndAppointmentEntity(
    val patientAndIdentifierEntity: PatientAndIdentifierEntity,
    val appointmentEntity: AppointmentEntity
)