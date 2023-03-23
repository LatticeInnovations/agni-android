package com.latticeonfhir.android.data.server.model.patient

import com.latticeonfhir.android.base.baseclass.ParcelableClass

data class PatientIdentifier(
    val identifierType: String,
    val identifierNumber: String,
    val code: String?
): ParcelableClass()
