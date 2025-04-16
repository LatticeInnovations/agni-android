package com.latticeonfhir.core.model.server.prescription.photo

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.util.Date

@Keep
data class PrescriptionPhotoResponse(
    val appointmentUuid: String,
    val prescriptionId: String,
    @SerializedName("prescriptionDocumentFhirId")
    val prescriptionFhirId: String?,
    val appointmentId: String,
    @SerializedName("patientId")
    val patientFhirId: String,
    val generatedOn: Date,
    @SerializedName("prescriptionFiles")
    val prescription: List<File>,
    val status: String?
)