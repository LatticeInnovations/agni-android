package com.latticeonfhir.android.data.local.model.labtest

import androidx.annotation.Keep
import java.util.Date

@Keep
data class LabTestLocal(
    val labTestId: String,
    val appointmentId: String,
    val patientId: String,
    val labTestFhirId: String?,
    val createdOn: Date,
)