package com.latticeonfhir.android.data.server.model.scheduleandappointment.appointment

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.latticeonfhir.android.data.server.model.scheduleandappointment.Slot
import kotlinx.parcelize.Parcelize
import java.util.Date

@Keep
@Parcelize
data class AppointmentResponse(
    val appointmentId: String?,
    val uuid: String,
    @SerializedName("patientId")
    val patientFhirId: String?,
    val scheduleId: String,
    val slot: Slot,
    val orgId: String,
    val createdOn: Date,
    val status: String
) : Parcelable
