package com.latticeonfhir.core.model.local.patch

import androidx.annotation.Keep
import com.latticeonfhir.android.data.local.model.patch.ChangeRequest

@Keep
data class AppointmentPatchRequest(
    val appointmentId: String,
    val status: ChangeRequest?,
    val slot: ChangeRequest?,
    val scheduleId: ChangeRequest?,
    val createdOn: ChangeRequest?
)