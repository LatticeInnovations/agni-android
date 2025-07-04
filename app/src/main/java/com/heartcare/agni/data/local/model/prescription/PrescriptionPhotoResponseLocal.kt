package com.heartcare.agni.data.local.model.prescription

import androidx.annotation.Keep
import com.heartcare.agni.data.server.model.prescription.photo.File
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
