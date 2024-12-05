package com.latticeonfhir.android.data.server.model.labormed.labtest

import androidx.annotation.Keep
import com.latticeonfhir.android.data.server.model.prescription.photo.File
import java.util.Date

@Keep
data class LabTestPhotoResponse(
    val labTestId: String,
    val appointmentId: String,
    val patientId: String,
    val labTestFhirId: String?,
    val createdOn: Date,
    val labTests: List<File>
)