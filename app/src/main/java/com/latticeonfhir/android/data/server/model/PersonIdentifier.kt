package com.latticeonfhir.android.data.server.model

import com.latticeonfhir.android.base.parcelclass.ParcelableClass

data class PersonIdentifier(
    val identifierType: String,
    val identifierNumber: String?,
    val code: String
): ParcelableClass()
