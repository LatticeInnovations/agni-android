package com.latticeonfhir.android.data.server.model.prescription.prescriptionresponse

import com.google.errorprone.annotations.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class PrescriptionResponse(
    @SerializedName("patientId")
    val patientFhirId: String,
    val generatedOn: Long,
    val prescriptionId: String,
    val prescriptionFhirId: String?,
    val prescription: List<Medication>
)

