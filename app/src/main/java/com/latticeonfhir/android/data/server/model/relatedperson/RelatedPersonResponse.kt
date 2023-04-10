package com.latticeonfhir.android.data.server.model.relatedperson

import androidx.annotation.Keep
import com.latticeonfhir.android.base.baseclass.ParcelableClass

@Keep
data class RelatedPersonResponse(
    val id: String,
    val relationship: List<Relationship>
): ParcelableClass()