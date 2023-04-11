package com.latticeonfhir.android.data.local.model

import com.latticeonfhir.android.data.local.enums.RelationEnum

data class RelationBetween (
    private val patientIs: RelationEnum,
    private val relativeIs: RelationEnum
)