package com.latticeonfhir.android.data.local.model.prescription

import androidx.annotation.Keep
import com.latticeonfhir.android.data.server.model.prescription.prescriptionresponse.Medication
import java.util.Date

@Keep
data class PrescriptionResponseLocal(
    val patientId: String,
    val patientFhirId: String?,
    val generatedOn: Date,
    val prescriptionId: String,
    val prescription: List<Medication>
)
