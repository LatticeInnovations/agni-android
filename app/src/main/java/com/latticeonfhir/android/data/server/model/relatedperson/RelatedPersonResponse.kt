package com.latticeonfhir.android.data.server.model.relatedperson

import com.latticeonfhir.android.base.baseclass.ParcelableClass

data class RelatedPersonResponse(
    val id: String,
    val relationship: List<Relationship>
): ParcelableClass()