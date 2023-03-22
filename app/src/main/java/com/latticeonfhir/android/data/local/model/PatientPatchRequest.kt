package com.latticeonfhir.android.data.local.model

data class PatientPatchRequest (
    val id: String,
    val identifier: MutableList<IdentifierPatchRequest>?,
)