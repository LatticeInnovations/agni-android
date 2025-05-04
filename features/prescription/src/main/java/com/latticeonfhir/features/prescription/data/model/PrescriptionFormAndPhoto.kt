package com.latticeonfhir.features.prescription.data.model

import java.util.Date

data class PrescriptionFormAndPhoto(
    val date: Date,
    val type: String,
    val prescription: Any
)
