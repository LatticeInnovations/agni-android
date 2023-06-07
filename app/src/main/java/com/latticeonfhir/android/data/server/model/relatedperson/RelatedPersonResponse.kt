package com.latticeonfhir.android.data.server.model.relatedperson

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class RelatedPersonResponse(
    val id: String,
    val relationship: List<Relationship>
): Parcelable