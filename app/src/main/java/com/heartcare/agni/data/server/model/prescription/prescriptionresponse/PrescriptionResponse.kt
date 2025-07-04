package com.heartcare.agni.data.server.model.prescription.prescriptionresponse

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.util.Date

@Keep
data class PrescriptionResponse(
    @SerializedName("patientId")
    val patientFhirId: String,
    val generatedOn: Date,
    val appointmentUuid: String,
    val appointmentId: String,
    val prescriptionId: String,
    val prescriptionFhirId: String?,
    val prescription: List<Medication>
)

