package com.latticeonfhir.android.data.local.model.relation

import androidx.annotation.Keep
import org.hl7.fhir.r4.model.Patient

@Keep
data class RelationFhir (
    val patient: Patient,
    val relative: Patient,
    val relation: String
)