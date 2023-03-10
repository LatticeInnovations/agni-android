package com.latticeonfhir.android.data.server.model

import com.latticeonfhir.android.base.parcelclass.ParcelableClass

data class PatientIdentifier(
    val identifierType: String,
    val identifierNumber: String,
    val code: String?
): ParcelableClass()
