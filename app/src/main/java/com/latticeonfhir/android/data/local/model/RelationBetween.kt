package com.latticeonfhir.android.data.local.model

import androidx.lifecycle.LiveData
import com.latticeonfhir.android.data.local.enums.RelationEnum

data class RelationBetween (
    private val patientIs: LiveData<RelationEnum>,
    private val relativeIs: LiveData<RelationEnum>
)