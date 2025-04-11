package com.latticeonfhir.core.model.server.labormed.labtest

import androidx.annotation.Keep
import com.latticeonfhir.core.model.server.prescription.photo.File
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