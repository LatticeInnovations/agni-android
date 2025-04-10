package com.latticeonfhir.core.data.local.roomdb.entities.patient

import androidx.annotation.Keep
import com.latticeonfhir.core.data.local.roomdb.entities.appointment.AppointmentEntity

@Keep
data class PatientAndIdentifierAndAppointmentEntity(
    val patientAndIdentifierEntity: PatientAndIdentifierEntity,
    val appointmentEntity: AppointmentEntity
)