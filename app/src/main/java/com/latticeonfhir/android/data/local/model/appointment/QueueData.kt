package com.latticeonfhir.android.data.local.model.appointment

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import org.hl7.fhir.r4.model.Appointment
import org.hl7.fhir.r4.model.Encounter
import org.hl7.fhir.r4.model.Patient

@Keep
@Parcelize
data class QueueData(
    val encounter: Encounter,
    val patient: Patient,
    val appointment: Appointment
): Parcelable
