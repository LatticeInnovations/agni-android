package com.latticeonfhir.android.data.local.model

import androidx.annotation.Keep
import androidx.lifecycle.LiveData
import com.latticeonfhir.android.data.local.enums.RelationEnum

@Keep
data class RelationBetween (
    val patientIs: LiveData<RelationEnum?>,
    val relativeIs: LiveData<RelationEnum?>
)