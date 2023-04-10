package com.latticeonfhir.android.data.server.model.patient

import androidx.annotation.Keep
import com.latticeonfhir.android.base.baseclass.ParcelableClass

@Keep
data class PatientIdentifier(
    val identifierType: String,
    val identifierNumber: String,
    val code: String?
): ParcelableClass()
