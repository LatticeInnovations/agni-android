package com.latticeonfhir.android.data.local.model.labtest

import androidx.annotation.Keep
import com.latticeonfhir.android.data.server.model.prescription.photo.File
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