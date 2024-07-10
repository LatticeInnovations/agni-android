package com.latticeonfhir.android.data.server.model.prescription.photo

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.util.Date

@Keep
data class PrescriptionPhotoResponse(
    val appointmentUuid: String,
    val prescriptionId: String,
    val prescriptionFhirId: String?,
    val appointmentId: String,
    @SerializedName("patientId")
    val patientFhirId: String,
    val generatedOn: Date,
    val prescription: List<File>
)