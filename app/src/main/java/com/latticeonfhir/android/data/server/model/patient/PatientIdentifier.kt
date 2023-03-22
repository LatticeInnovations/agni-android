package com.latticeonfhir.android.data.server.model.patient

import com.latticeonfhir.android.base.parcelclass.ParcelableClass
import com.latticeonfhir.android.data.local.model.Unique

data class PatientIdentifier(
    val identifierType: String,
    val identifierNumber: String,
    val code: String?
): ParcelableClass(), Unique<String> {
    override val type: String
        get() = identifierType
}
