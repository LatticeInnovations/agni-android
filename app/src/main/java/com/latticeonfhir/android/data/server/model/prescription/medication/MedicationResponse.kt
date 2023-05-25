package com.latticeonfhir.android.data.server.model.prescription.medication

import com.google.errorprone.annotations.Keep

@Keep
data class MedicationResponse(
    val medications: List<Medication>
)