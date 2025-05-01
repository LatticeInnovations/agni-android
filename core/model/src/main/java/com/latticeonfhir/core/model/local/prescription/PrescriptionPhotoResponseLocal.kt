package com.latticeonfhir.core.model.local.prescription

import androidx.annotation.Keep
import com.latticeonfhir.core.model.server.prescription.photo.File
import java.util.Date

@Keep
data class PrescriptionPhotoResponseLocal(
    val patientId: String,
    val patientFhirId: String?,
    val appointmentId: String,
    val generatedOn: Date,
    val prescriptionId: String,
    val prescription: List<File>,
    val prescriptionFhirId: String?
)
