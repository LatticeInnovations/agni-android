package com.latticeonfhir.android.data.local.model.patch

import androidx.annotation.Keep

@Keep
data class AppointmentPatchRequest(
    val appointmentId: String,
    val status: ChangeRequest?,
    val slot: ChangeRequest?,
    val scheduleId: ChangeRequest?,
    val createdOn: ChangeRequest?
)