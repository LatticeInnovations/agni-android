package com.heartcare.agni.data.local.roomdb.entities.patient

import androidx.annotation.Keep
import com.heartcare.agni.data.local.roomdb.entities.appointment.AppointmentEntity

@Keep
data class PatientAndIdentifierAndAppointmentEntity(
    val patientAndIdentifierEntity: PatientAndIdentifierEntity,
    val appointmentEntity: AppointmentEntity
)