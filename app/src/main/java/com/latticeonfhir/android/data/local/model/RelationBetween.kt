package com.latticeonfhir.android.data.local.model

import com.latticeonfhir.android.data.local.enums.RelationEnum

data class RelationBetween (
    val patientIs: RelationEnum?,
    val relativeIs: RelationEnum?
)