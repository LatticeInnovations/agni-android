package com.latticeonfhir.android.data.local.model.states

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class State(
    val districts: List<District>,
    @SerializedName("state_code")
    val stateCode: String,
    @SerializedName("state_name")
    val stateName: String
)