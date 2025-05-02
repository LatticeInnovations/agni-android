package com.latticeonfhir.core.model.local.appointment

import android.os.Parcelable
import androidx.annotation.Keep
import com.latticeonfhir.core.model.server.scheduleandappointment.Slot
import kotlinx.parcelize.Parcelize
import java.util.Date

@Keep
@Parcelize
data class AppointmentResponseLocal(
    val appointmentId: String?,
    val uuid: String,
    val patientId: String,
    val scheduleId: Date,
    val slot: Slot,
    val orgId: String,
    val createdOn: Date,
    val status: String,
    val appointmentType: String,
    val inProgressTime: Date?
) : Parcelable