package com.latticeonfhir.android.data.server.model.prescription.prescriptionresponse

import com.google.errorprone.annotations.Keep
import com.google.gson.annotations.SerializedName
import java.util.Date

@Keep
data class PrescriptionResponse(
    @SerializedName("patientId")
    val patientFhirId: String,
    val generatedOn: Date,
    val prescriptionId: String,
    val prescriptionFhirId: String?,
    val prescription: List<Medication>
)

