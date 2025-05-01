package com.latticeonfhir.core.model.local.labtest

import androidx.annotation.Keep
import com.latticeonfhir.core.model.server.prescription.photo.File
import java.util.Date

@Keep
data class LabTestPhotoResponseLocal(
    val labTestId: String,
    val appointmentId: String,
    val patientId: String,
    val labTestFhirId: String? = null,
    val createdOn: Date,
    val labTests: List<File>
)