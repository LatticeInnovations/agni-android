package com.latticeonfhir.core.data.server.model.relatedperson

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class Relationship(
    val patientIs: String,
    val relativeId: String
) : Parcelable